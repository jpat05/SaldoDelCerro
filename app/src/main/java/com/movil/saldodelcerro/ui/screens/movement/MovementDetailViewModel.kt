package com.movil.saldodelcerro.ui.screens.movement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.ui.UiState
import com.movil.saldodelcerro.ui.navigation.MOVEMENT_ID_ARG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Detalle de un movimiento. Es el unico ViewModel que recibe un argumento de navegacion.
 *
 * El id no se lee como texto en la pantalla: lo entrega el framework dentro del [SavedStateHandle]
 * y aca se resuelve, de modo que el composable nunca toca el `Bundle` de argumentos.
 */
class MovementDetailViewModel(
    private val ledgerService: LedgerService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movementId: Long = checkNotNull(savedStateHandle.get<Long>(MOVEMENT_ID_ARG))

    private val _uiState = MutableStateFlow<UiState<Movement>>(UiState.Loading)
    val uiState: StateFlow<UiState<Movement>> = _uiState.asStateFlow()

    init {
        load()
    }

    /** Carga el movimiento. Publico para poder reintentar desde la pantalla de error. */
    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val movement = ledgerService.movement(movementId)
            _uiState.value = if (movement != null) {
                UiState.Success(movement)
            } else {
                UiState.Error("No se encontró el movimiento solicitado.")
            }
        }
    }
}