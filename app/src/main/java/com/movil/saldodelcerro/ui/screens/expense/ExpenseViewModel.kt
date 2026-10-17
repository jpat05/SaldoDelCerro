package com.movil.saldodelcerro.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.LedgerResult
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.UiFeedback
import com.movil.saldodelcerro.ui.defaultMessage
import com.movil.saldodelcerro.utils.sanitizeMoneyInput
import com.movil.saldodelcerro.utils.toMoneyOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val ledgerService: LedgerService,
    private val categoryService: CategoryService
) : ViewModel() {

    private data class FormState(
        val amountInput: String = "",
        val reasonInput: String = "",
        val selectedId: Long = 0L,
        val submitting: Boolean = false,
        val feedback: UiFeedback? = null
    )

    private val form = MutableStateFlow(FormState())

    val uiState: StateFlow<ExpenseUiState> = combine(categoryService.expenseCategories, form) { categories, state ->
        ExpenseUiState(
            amountInput = state.amountInput,
            reasonInput = state.reasonInput,
            selectedCategoryId = state.selectedId
                .takeIf { id -> categories.any { it.id == id } }
                ?: categories.firstOrNull()?.id
                ?: 0L,
            categories = categories,
            isSubmitting = state.submitting,
            feedback = state.feedback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExpenseUiState()
    )

    fun onAmountChange(value: String) =
        form.update { it.copy(amountInput = value.sanitizeMoneyInput(), feedback = null) }

    fun onReasonChange(value: String) =
        form.update { it.copy(reasonInput = value, feedback = null) }

    fun onCategorySelected(id: Long) =
        form.update { it.copy(selectedId = id) }

    fun dismissFeedback() =
        form.update { it.copy(feedback = null) }

    fun addCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryService.add(CategoryEntity(name = name.trim(), isIncome = false))
        }
    }

    fun submit() {
        val state = uiState.value
        val category = state.categories.find { it.id == state.selectedCategoryId } ?: return

        val amount = state.amountInput.toMoneyOrNull()
        if (amount == null) {
            form.update { it.copy(feedback = UiFeedback("El monto debe ser mayor que cero.", true)) }
            return
        }

        viewModelScope.launch {
            form.update { it.copy(submitting = true) }

            val result = ledgerService.registerMovement(
                TransactionEntity(
                    reason = state.reasonInput.ifBlank { "Egreso" },
                    amount = amount,
                    typeId = category.id
                ),
                category
            )

            form.update { current ->
                when (result) {
                    LedgerResult.Accepted -> current.copy(
                        amountInput = "",
                        reasonInput = "",
                        submitting = false,
                        feedback = UiFeedback("¡Gasto registrado con éxito!", false)
                    )
                    is LedgerResult.Rejected -> current.copy(
                        submitting = false,
                        feedback = UiFeedback(result.reason.defaultMessage(isIncome = false), true)
                    )
                }
            }
        }
    }
}

data class ExpenseUiState(
    val amountInput: String = "",
    val reasonInput: String = "",
    val selectedCategoryId: Long = 0L,
    val categories: List<CategoryEntity> = emptyList(),
    val isSubmitting: Boolean = false,
    val feedback: UiFeedback? = null
)
