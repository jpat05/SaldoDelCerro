package com.movil.saldodelcerro.ui

/**
 * Evento puntual (one-off) de una pantalla: algo que se consume una sola vez y que, por lo tanto,
 * no debe formar parte del estado persistente del ViewModel.
 *
 * Se emiten por un `Channel` y se exponen como `Flow` ([kotlinx.coroutines.flow.receiveAsFlow]),
 * de modo que un cambio de configuracion no los vuelva a disparar.
 */
sealed interface UiEvent {

    /** Mensaje transitorio (Snackbar) de exito o de error. */
    data class ShowMessage(val message: String, val isError: Boolean) : UiEvent
}