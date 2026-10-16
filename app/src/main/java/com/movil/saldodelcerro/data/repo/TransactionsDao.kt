package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.movil.saldodelcerro.data.models.CategoryTotal
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface TransactionsDao {
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY generatedDate DESC")
    fun getAllTransactionsOrderByDateDesc(): Flow<List<TransactionDto>>

    @Query("SELECT SUM(amount) FROM transactions")
    fun getTotalBalance(): Flow<BigDecimal>

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :transactionsId")
    fun getAllTransactionsById(transactionsId: Long): TransactionDto?

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN tt.isIncome = 1 THEN t.amount ELSE -t.amount END), 0)
        FROM transactions t
        INNER JOIN transaction_types tt ON t.typeId = tt.id
    """)
    fun getAvailableBalance(): Flow<BigDecimal>

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t INNER JOIN transaction_types tt ON t.typeId = tt.id
        WHERE tt.isIncome = 1
    """)
    fun getTotalIncome(): Flow<BigDecimal>

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t INNER JOIN transaction_types tt ON t.typeId = tt.id
        WHERE tt.isIncome = 0
    """)
    fun getTotalExpense(): Flow<BigDecimal>

    /** Ingresos desde [sinceMillis], excluyendo el tipo [excludedTypeName]. */
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t INNER JOIN transaction_types tt ON t.typeId = tt.id
        WHERE tt.isIncome = 1 AND t.generatedDate >= :sinceMillis AND tt.name <> :excludedTypeName
    """)
    fun getIncomeSince(sinceMillis: Long, excludedTypeName: String): Flow<BigDecimal>

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t INNER JOIN transaction_types tt ON t.typeId = tt.id
        WHERE tt.isIncome = 0 AND t.generatedDate >= :sinceMillis
    """)
    fun getExpenseSince(sinceMillis: Long): Flow<BigDecimal>

    @Query("""
        SELECT tt.name AS name, COALESCE(SUM(t.amount), 0) AS total
        FROM transaction_types tt LEFT JOIN transactions t ON t.typeId = tt.id
        WHERE tt.isIncome = 0
        GROUP BY tt.name
    """)
    fun getCategoryTotals(): Flow<List<CategoryTotal>>

    @Query("DELETE FROM transactions WHERE typeId = :typeId")
    suspend fun deleteTransactionsByType(typeId: Long)

    @Transaction
    @Query("""
        SELECT * FROM transactions
        WHERE typeId = (SELECT id FROM transaction_types WHERE name = 'Capital base' LIMIT 1)
        ORDER BY generatedDate DESC LIMIT 1
    """)
    suspend fun getCapitalTransaction(): TransactionDto?
}