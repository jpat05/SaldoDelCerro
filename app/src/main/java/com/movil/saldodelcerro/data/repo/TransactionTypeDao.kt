package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movil.saldodelcerro.data.models.TransactionTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionTypeDao {
    @Query("SELECT * FROM transaction_types ORDER BY name ASC")
    fun getAllTypeOrderByAsc(): Flow<List<TransactionTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertType(type: TransactionTypeEntity): Long

    @Delete
    suspend fun deleteType(type: TransactionTypeEntity)

    @Query("SELECT * FROM transaction_types WHERE name = :name LIMIT 1")
    suspend fun getTypeByName(name: String): TransactionTypeEntity?

    @Query("SELECT COUNT(*) FROM transaction_types")
    suspend fun count(): Int}