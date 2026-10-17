package com.movil.saldodelcerro.ui

import com.movil.saldodelcerro.data.models.LedgerResult

/** Transient success/error message surfaced by a screen. */
data class UiFeedback(val message: String, val isError: Boolean)

/** Maps a rejection reason to the message shown to the user. */
fun LedgerResult.Reason.defaultMessage(isIncome: Boolean): String = when (this) {
    LedgerResult.Reason.INSUFFICIENT_FUNDS ->
        if (isIncome) "No se pudo registrar el ingreso."
        else "Saldo insuficiente para registrar este gasto."
    LedgerResult.Reason.NON_POSITIVE_AMOUNT -> "El monto debe ser mayor que cero."
}
