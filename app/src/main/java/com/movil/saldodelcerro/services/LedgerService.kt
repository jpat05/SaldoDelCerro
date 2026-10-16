package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.data.models.CategoryTotal
import com.movil.saldodelcerro.data.models.TransactionDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

/**
 * Derived financial state. All arithmetic happens here, in Kotlin [BigDecimal] — never in SQL,
 * because `amount` is persisted as TEXT and SQLite would coerce `SUM` to REAL.
 *
 * There is no opening balance / capital concept: the ledger starts at 0 and is always
 * `totalIncome − totalExpense`.
 */
class LedgerService(
    private val transactionService: TransactionService
) {
    val totalIncome: Flow<BigDecimal> = transactionService.transactions
        .map { transactions -> transactions.filter { it.type.isIncome }.sumAmounts() }

    val totalExpense: Flow<BigDecimal> = transactionService.transactions
        .map { transactions -> transactions.filter { !it.type.isIncome }.sumAmounts() }

    val availableBalance: Flow<BigDecimal> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }

    val categoryTotals: Flow<List<CategoryTotal>> = transactionService.transactions
        .map { transactions ->
            transactions
                .filter { !it.type.isIncome }
                .groupBy { it.type.name }
                .map { (name, movements) -> CategoryTotal(name = name, total = movements.sumAmounts()) }
        }

    private fun List<TransactionDto>.sumAmounts(): BigDecimal =
        fold(BigDecimal.ZERO) { acc, dto -> acc + dto.transaction.amount }
}
