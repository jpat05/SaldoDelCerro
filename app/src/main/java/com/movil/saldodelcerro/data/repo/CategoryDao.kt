package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movil.saldodelcerro.data.models.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM transaction_types ORDER BY name ASC")
    fun getAllOrderByAsc(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM transaction_types")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: CategoryEntity): Long

    @Delete
    suspend fun delete(category: CategoryEntity)
}
