package com.movil.saldodelcerro.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.domain.model.LedgerResult
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.UiEvent
import com.movil.saldodelcerro.ui.UiState
import com.movil.saldodelcerro.ui.defaultMessage
import com.movil.saldodelcerro.utils.sanitizeMoneyInput
import com.movil.saldodelcerro.utils.toMoneyOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val ledgerService: LedgerService,
    private val categoryService: CategoryService
) : ViewModel() {

    /** Estado del formulario: solo lo que el usuario teclea y el flag de envio en curso. */
    private data class FormState(
        val amountInput: String = "",
        val reasonInput: String = "",
        val selectedId: Long = 0L,
        val submitting: Boolean = false
    )

    private val form = MutableStateFlow(FormState())

    /**
     * Mensajes transitorios (exito / rechazo). Van por un `Channel` y no dentro del estado, porque
     * son eventos puntuales: un cambio de configuracion no debe volver a mostrarlos.
     */
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    private val expenseState: Flow<ExpenseUiState> =
        combine(categoryService.expenseCategories, form) { categories, state ->
            ExpenseUiState(
                amountInput = state.amountInput,
                reasonInput = state.reasonInput,
                selectedCategoryId = state.selectedId
                    .takeIf { id -> categories.any { it.id == id } }
                    ?: categories.firstOrNull()?.id
                    ?: 0L,
                categories = categories,
                isSubmitting = state.submitting
            )
        }

    val uiState: StateFlow<UiState<ExpenseUiState>> = expenseState
        .map<ExpenseUiState, UiState<ExpenseUiState>> { data -> UiState.Success(data) }
        .catch { emit(UiState.Error("No se pudieron cargar las categorías de egreso.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun onAmountChange(value: String) =
        form.update { it.copy(amountInput = value.sanitizeMoneyInput()) }

    fun onReasonChange(value: String) =
        form.update { it.copy(reasonInput = value) }

    fun onCategorySelected(id: Long) =
        form.update { it.copy(selectedId = id) }

    fun addCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryService.add(name, isIncome = false)
        }
    }

    fun submit() {
        val snapshot = (uiState.value as? UiState.Success<ExpenseUiState>)?.data ?: return
        val category = snapshot.categories.find { it.id == snapshot.selectedCategoryId } ?: return

        val amount = snapshot.amountInput.toMoneyOrNull()
        if (amount == null) {
            _events.trySend(UiEvent.ShowMessage("El monto debe ser mayor que cero.", isError = true))
            return
        }

        viewModelScope.launch {
            form.update { it.copy(submitting = true) }

            val result = ledgerService.registerMovement(
                reason = snapshot.reasonInput,
                amount = amount,
                category = category
            )

            when (result) {
                LedgerResult.Accepted -> {
                    form.update { it.copy(amountInput = "", reasonInput = "", submitting = false) }
                    _events.send(
                        UiEvent.ShowMessage("¡Gasto registrado con éxito!", isError = false)
                    )
                }

                is LedgerResult.Rejected -> {
                    form.update { it.copy(submitting = false) }
                    _events.send(
                        UiEvent.ShowMessage(
                            result.reason.defaultMessage(isIncome = false),
                            isError = true
                        )
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
    val categories: List<Category> = emptyList(),
    val isSubmitting: Boolean = false
)