package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.data.models.CategoryTotal
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.models.TransactionTypeEntity
import com.movil.saldodelcerro.data.models.UserEntity
import com.movil.saldodelcerro.data.models.UserRole
import com.movil.saldodelcerro.data.repo.TransactionTypeDao
import com.movil.saldodelcerro.data.repo.TransactionsDao
import com.movil.saldodelcerro.data.repo.UserDao
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

class WalletService(
    private val userDao: UserDao,
    private val transactionTypeDao: TransactionTypeDao,
    private val transactionDao: TransactionsDao
) {
    suspend fun getUserByRole(role: UserRole): UserEntity? = userDao.getUserByRole(role)
    suspend fun saveUser(user: UserEntity) = userDao.saveUser(user)

    val allTypes: Flow<List<TransactionTypeEntity>> = transactionTypeDao.getAllTypeOrderByAsc()

    suspend fun insertType(type: TransactionTypeEntity): Long = transactionTypeDao.insertType(type)
    suspend fun deleteType(type: TransactionTypeEntity) = transactionTypeDao.deleteType(type)

    val allTransactionsDto: Flow<List<TransactionDto>> = transactionDao.getAllTransactionsOrderByDateDesc()

    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)

    val availableBalance: Flow<BigDecimal> = transactionDao.getAvailableBalance()
    val totalIncome: Flow<BigDecimal> = transactionDao.getTotalIncome()
    val totalExpense: Flow<BigDecimal> = transactionDao.getTotalExpense()
    val categoryTotals: Flow<List<CategoryTotal>> = transactionDao.getCategoryTotals()

    suspend fun getCapitalBase(): TransactionDto? = transactionDao.getCapitalTransaction()

    suspend fun saveCapitalBase(amount: BigDecimal) {
        val capitalType = getOrCreateCapitalType()
        transactionDao.deleteTransactionsByType(capitalType.id)
        transactionDao.insertTransaction(
            TransactionEntity(reason = CAPITAL_TYPE_NAME, amount = amount, typeId = capitalType.id)
        )
    }

    private suspend fun getOrCreateCapitalType(): TransactionTypeEntity {
        transactionTypeDao.getTypeByName(CAPITAL_TYPE_NAME)?.let { return it }
        val id = transactionTypeDao.insertType(TransactionTypeEntity(name = CAPITAL_TYPE_NAME, isIncome = true))
        return TransactionTypeEntity(id = id, name = CAPITAL_TYPE_NAME, isIncome = true)
    }

    companion object {
        const val CAPITAL_TYPE_NAME = "Capital base"
    }
}