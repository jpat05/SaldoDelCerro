package com.movil.saldodelcerro.domain.repository

import com.movil.saldodelcerro.domain.model.Movement
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/** Persistence contract for ledger movements, expressed in domain types. */
interface MovementRepository {

    fun observeAll(): Flow<List<Movement>>

    /**
     * One movement by id, for the detail destination. A single-record fetch is a one-shot read, so
     * it returns a value instead of a stream — the list queries above are the ones that drive
     * reactive UI.
     */
    suspend fun findById(id: Long): Movement?

    suspend fun insert(reason: String, amount: BigDecimal, categoryId: Long)

    suspend fun delete(movement: Movement)
}
