package com.movil.saldodelcerro.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movil.saldodelcerro.data.models.CategoryTotal
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.services.LedgerService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

class SummaryViewModel(ledgerService: LedgerService) : ViewModel() {

    val uiState: StateFlow<SummaryUiState> = combine(
        ledgerService.availableBalance,
        ledgerService.totalIncome,
        ledgerService.totalExpense,
        ledgerService.categoryTotals,
        ledgerService.movements
    ) { balance, income, expense, totals, movements ->
        SummaryUiState(
            balance = balance,
            totalIncome = income,
            totalExpense = expense,
            categoryTotals = totals,
            movements = movements
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SummaryUiState()
    )
}

data class SummaryUiState(
    val balance: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val movements: List<TransactionDto> = emptyList()
)
