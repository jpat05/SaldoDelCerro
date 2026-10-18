package com.movil.saldodelcerro.domain.repository

import com.movil.saldodelcerro.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Persistence contract for categories, expressed in domain types.
 *
 * The implementation lives in the data layer and is the only place that knows about Room. The
 * services and the UI depend on this interface alone, so swapping the storage engine (or adding a
 * remote source next to the local one) does not reach past this line.
 */
interface CategoryRepository {

    /** Every row, archived included — the service decides what is user-visible. */
    fun observeAll(): Flow<List<Category>>

    suspend fun insert(category: Category): Long

    /** Soft delete / restore. Hard delete is intentionally not exposed (FK is RESTRICT). */
    suspend fun setActive(id: Long, active: Boolean)
}
