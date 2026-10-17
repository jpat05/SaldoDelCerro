package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.CategoryTotal
import com.movil.saldodelcerro.data.models.LedgerResult
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Derived financial state plus the ledger's write invariants. All arithmetic happens here, in
 * Kotlin [BigDecimal] — never in SQL, because `amount` is persisted as TEXT and SQLite would
 * coerce `SUM` to REAL.
 *
 * There is no opening balance / capital concept: the ledger starts at 0 and is always
 * `totalIncome − totalExpense`. Only movements whose category is still `active` count, so a
 * soft-deleted category drops out of the books without losing its history.
 */
class LedgerService(
    private val transactionService: TransactionService,
    private val categoryService: CategoryService
) {
    /** Serializes the read-modify-write cycle that guards the balance invariant. */
    private val writeLock = Mutex()

    /** Movements whose category is still active — the ledger's source of truth. */
    val movements: Flow<List<TransactionDto>> = transactionService.transactions
        .map { list -> list.filter { it.type.active } }

    val totalIncome: Flow<BigDecimal> = movements
        .map { list -> list.filter { it.type.isIncome }.sumAmounts() }

    val totalExpense: Flow<BigDecimal> = movements
        .map { list -> list.filter { !it.type.isIncome }.sumAmounts() }

    val availableBalance: Flow<BigDecimal> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }

    val categoryTotals: Flow<List<CategoryTotal>> = movements
        .map { list ->
            list.filter { !it.type.isIncome }
                .groupBy { it.type.name }
                .map { (name, rows) -> CategoryTotal(name = name, total = rows.sumAmounts()) }
        }

    /**
     * The single money-in / money-out entry point. Income is always accepted; an expense is
     * rejected when it exceeds the available balance, so the ledger can never go negative.
     */
    suspend fun registerMovement(
        transaction: TransactionEntity,
        category: CategoryEntity
    ): LedgerResult = writeLock.withLock {
        if (transaction.amount <= BigDecimal.ZERO) {
            return@withLock LedgerResult.Rejected(LedgerResult.Reason.NON_POSITIVE_AMOUNT)
        }

        val movement = transaction.copy(
            amount = transaction.amount.setScale(2, RoundingMode.HALF_UP),
            typeId = category.id
        )

        if (!category.isIncome && movement.amount > movements.first().balance()) {
            return@withLock LedgerResult.Rejected(LedgerResult.Reason.INSUFFICIENT_FUNDS)
        }

        transactionService.add(movement)
        LedgerResult.Accepted
    }

    /**
     * Soft-deletes a category. Archiving an income category removes its income from the ledger,
     * so it is refused when that would push the balance below zero. Archiving an expense category
     * only ever raises the balance, so it is always accepted.
     */
    suspend fun archiveCategory(category: CategoryEntity): LedgerResult = writeLock.withLock {
        val active = movements.first()

        if (category.isIncome) {
            val archivedIncome = active.filter { it.transaction.typeId == category.id }.sumAmounts()
            if (active.balance() - archivedIncome < BigDecimal.ZERO) {
                return@withLock LedgerResult.Rejected(LedgerResult.Reason.INSUFFICIENT_FUNDS)
            }
        }

        categoryService.archive(category)
        LedgerResult.Accepted
    }

    private fun List<TransactionDto>.balance(): BigDecimal =
        filter { it.type.isIncome }.sumAmounts() - filter { !it.type.isIncome }.sumAmounts()

    private fun List<TransactionDto>.sumAmounts(): BigDecimal =
        fold(BigDecimal.ZERO) { acc, dto -> acc + dto.transaction.amount }
}
