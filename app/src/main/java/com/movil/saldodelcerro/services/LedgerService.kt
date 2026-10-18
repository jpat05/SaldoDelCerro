package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.domain.model.CategoryTotal
import com.movil.saldodelcerro.domain.model.LedgerResult
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.domain.repository.MovementRepository
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
 *
 * Everything this class exposes — and everything it accepts — is a domain type, so neither the
 * ViewModels nor the Composables ever see a Room entity. Writes go straight to
 * [MovementRepository]: recording a movement is one insert, and the rules that matter (positive
 * amount, balance never negative, default label) belong to the ledger rather than to a layer that
 * would only forward the call.
 */
class LedgerService(
    private val movementRepository: MovementRepository,
    private val categoryService: CategoryService
) {
    /** Serializes the read-modify-write cycle that guards the balance invariant. */
    private val writeLock = Mutex()

    /** Movements whose category is still active — the ledger's source of truth. */
    val movements: Flow<List<Movement>> = movementRepository.observeAll()
        .map { list -> list.filter { it.categoryActive } }

    val totalIncome: Flow<BigDecimal> = movements
        .map { list -> list.filter { it.isIncome }.sumAmounts() }

    val totalExpense: Flow<BigDecimal> = movements
        .map { list -> list.filter { !it.isIncome }.sumAmounts() }

    val availableBalance: Flow<BigDecimal> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }

    val categoryTotals: Flow<List<CategoryTotal>> = movements
        .map { list ->
            list.filter { !it.isIncome }
                .groupBy { it.categoryName }
                .map { (name, rows) -> CategoryTotal(name = name, total = rows.sumAmounts()) }
        }

    /**
     * The single money-in / money-out entry point. Income is always accepted; an expense is
     * rejected when it exceeds the available balance, so the ledger can never go negative.
     *
     * A blank [reason] gets the default label for the movement's direction, so every caller stores
     * the same readable text instead of each screen inventing its own fallback.
     */
    suspend fun registerMovement(
        reason: String,
        amount: BigDecimal,
        category: Category
    ): LedgerResult = writeLock.withLock {
        if (amount <= BigDecimal.ZERO) {
            return@withLock LedgerResult.Rejected(LedgerResult.Reason.NON_POSITIVE_AMOUNT)
        }

        val normalized = amount.setScale(2, RoundingMode.HALF_UP)

        if (!category.isIncome && normalized > movements.first().balance()) {
            return@withLock LedgerResult.Rejected(LedgerResult.Reason.INSUFFICIENT_FUNDS)
        }

        movementRepository.insert(
            reason = reason.ifBlank { if (category.isIncome) "Ingreso" else "Egreso" },
            amount = normalized,
            categoryId = category.id
        )
        LedgerResult.Accepted
    }

    /**
     * One movement by id, for the detail destination. Deliberately unfiltered: a movement whose
     * category was archived still has to be readable from its detail screen, even though it no
     * longer counts in the books.
     */
    suspend fun movement(id: Long): Movement? = movementRepository.findById(id)

    /**
     * Soft-deletes a category. Archiving an income category removes its income from the ledger,
     * so it is refused when that would push the balance below zero. Archiving an expense category
     * only ever raises the balance, so it is always accepted.
     */
    suspend fun archiveCategory(category: Category): LedgerResult = writeLock.withLock {
        val active = movements.first()

        if (category.isIncome) {
            val archivedIncome = active.filter { it.categoryId == category.id }.sumAmounts()
            if (active.balance() - archivedIncome < BigDecimal.ZERO) {
                return@withLock LedgerResult.Rejected(LedgerResult.Reason.INSUFFICIENT_FUNDS)
            }
        }

        categoryService.archive(category)
        LedgerResult.Accepted
    }

    private fun List<Movement>.balance(): BigDecimal =
        filter { it.isIncome }.sumAmounts() - filter { !it.isIncome }.sumAmounts()

    private fun List<Movement>.sumAmounts(): BigDecimal =
        fold(BigDecimal.ZERO) { acc, movement -> acc + movement.amount }
}
