package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.repo.TransactionsDao
import kotlinx.coroutines.flow.Flow

/**
 * Movement layer: one write path for both money-in and money-out.
 * Whether a movement is income or expense is decided by its category's `isIncome`.
 */
class TransactionService(
    private val transactionsDao: TransactionsDao
) {
    val transactions: Flow<List<TransactionDto>> = transactionsDao.getAllOrderByDateDesc()

    suspend fun add(transaction: TransactionEntity) = transactionsDao.insert(transaction)

    suspend fun remove(transaction: TransactionEntity) = transactionsDao.delete(transaction)
}
