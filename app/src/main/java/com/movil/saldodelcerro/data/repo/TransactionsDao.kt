package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
}