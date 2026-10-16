package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDao {
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY generatedDate DESC")
    fun getAllOrderByDateDesc(): Flow<List<TransactionDto>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getById(id: Long): TransactionDto?

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}
