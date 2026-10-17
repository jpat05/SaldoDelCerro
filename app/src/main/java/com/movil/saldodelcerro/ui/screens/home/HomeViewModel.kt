package com.movil.saldodelcerro.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.services.LedgerService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

class HomeViewModel(ledgerService: LedgerService) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        ledgerService.availableBalance,
        ledgerService.totalIncome,
        ledgerService.totalExpense,
        ledgerService.movements
    ) { balance, income, expense, movements ->
        HomeUiState(
            balance = balance,
            totalIncome = income,
            totalExpense = expense,
            recentMovements = movements.take(5)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}

data class HomeUiState(
    val balance: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val recentMovements: List<TransactionDto> = emptyList()
)
