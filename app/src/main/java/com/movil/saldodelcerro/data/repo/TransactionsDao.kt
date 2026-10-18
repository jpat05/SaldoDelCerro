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

    /**
     * Single movement with its category already resolved. `suspend` on purpose: Room runs it on its
     * own executor instead of the caller's thread (a blocking DAO call throws on the main thread),
     * which is what the other two read paths have always done.
     */
    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionDto?

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}
