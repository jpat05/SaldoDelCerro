package com.movil.saldodelcerro.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movil.saldodelcerro.domain.model.CategoryTotal
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

class SummaryViewModel(ledgerService: LedgerService) : ViewModel() {

    private val summaryState: Flow<SummaryUiState> = combine(
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
    }

    val uiState: StateFlow<UiState<SummaryUiState>> = summaryState
        .map<SummaryUiState, UiState<SummaryUiState>> { data -> UiState.Success(data) }
        .catch { emit(UiState.Error("No se pudo cargar el resumen.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
}

data class SummaryUiState(
    val balance: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val movements: List<Movement> = emptyList()
)