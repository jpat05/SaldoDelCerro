package com.movil.saldodelcerro.data.repo

import com.movil.saldodelcerro.data.mapper.toDomain
import com.movil.saldodelcerro.data.mapper.toEntity
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.domain.model.Movement
import com.movil.saldodelcerro.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

/** Room-backed [MovementRepository]. */
class MovementRepositoryImpl(
    private val transactionsDao: TransactionsDao
) : MovementRepository {

    override fun observeAll(): Flow<List<Movement>> =
        transactionsDao.getAllOrderByDateDesc().map { rows -> rows.map { it.toDomain() } }

    override suspend fun findById(id: Long): Movement? =
        transactionsDao.getById(id)?.toDomain()

    override suspend fun insert(reason: String, amount: BigDecimal, categoryId: Long) =
        transactionsDao.insert(
            TransactionEntity(reason = reason, amount = amount, typeId = categoryId)
        )

    override suspend fun delete(movement: Movement) =
        transactionsDao.delete(movement.toEntity())
}
