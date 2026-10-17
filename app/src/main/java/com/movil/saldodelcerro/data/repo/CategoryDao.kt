package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movil.saldodelcerro.data.models.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    /** Every row, archived included — the service decides what is visible. */
    @Query("SELECT * FROM transaction_types ORDER BY name ASC")
    fun getAllOrderByAsc(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM transaction_types")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: CategoryEntity): Long

    /** One statement, so the whole bootstrap batch is applied atomically. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(categories: List<CategoryEntity>)

    /** Soft delete / restore. Hard delete is intentionally not exposed (FK is RESTRICT). */
    @Query("UPDATE transaction_types SET active = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}
