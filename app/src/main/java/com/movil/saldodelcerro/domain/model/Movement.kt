package com.movil.saldodelcerro.domain.model

import java.math.BigDecimal

/**
 * A ledger entry as the rest of the app sees it: the movement plus its category, already
 * resolved.
 *
 * The `@Relation` graph the persistence layer uses (`TransactionDto`) is flattened here, so no
 * Composable or ViewModel ever walks a Room relation or reads a table row. Money stays a
 * [BigDecimal] — the same value the entity stores as TEXT.
 */
data class Movement(
    val id: Long = 0L,
    val reason: String,
    val amount: BigDecimal,
    val date: Long = System.currentTimeMillis(),
    val categoryId: Long,
    val categoryName: String,
    val isIncome: Boolean,
    /**
     * Whether the movement's category is still visible. Archived categories stay on their
     * movements but drop out of the books, so the ledger only counts actions where this is true.
     */
    val categoryActive: Boolean = true
)
