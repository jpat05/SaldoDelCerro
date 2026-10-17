package com.movil.saldodelcerro.data.models

/**
 * Outcome of a ledger write (registering a movement, archiving a category) that a business rule
 * can reject. Kept as a value object so callers branch on a type instead of catching exceptions.
 */
sealed interface LedgerResult {
    data object Accepted : LedgerResult

    data class Rejected(val reason: Reason) : LedgerResult

    enum class Reason { INSUFFICIENT_FUNDS, NON_POSITIVE_AMOUNT }
}
