package com.movil.saldodelcerro.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class HomeViewModel(ledgerService: LedgerService) : ViewModel() {

    private val homeState: Flow<HomeUiState> = combine(
        ledgerService.availableBalance,
        ledgerService.totalIncome,
        ledgerService.totalExpense
    ) { balance, income, expense ->
        HomeUiState(
            balance = balance,
            totalIncome = income,
            totalExpense = expense
        )
    }

    /**
     * Un unico flujo de estado: `Loading` hasta la primera emision de Room, `Success` con los
     * totales, `Error` si la lectura falla.
     */
    val uiState: StateFlow<UiState<HomeUiState>> = homeState
        .map<HomeUiState, UiState<HomeUiState>> { data -> UiState.Success(data) }
        .catch { emit(UiState.Error("No se pudieron cargar tus finanzas.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
}

data class HomeUiState(
    val balance: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO
)