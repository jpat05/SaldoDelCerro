package com.movil.saldodelcerro.data.repo

import com.movil.saldodelcerro.data.mapper.toDomain
import com.movil.saldodelcerro.data.mapper.toEntity
import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed [CategoryRepository].
 *
 * The DAO stays a plain CRUD contract; turning rows into domain objects happens here and nowhere
 * else.
 */
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun observeAll(): Flow<List<Category>> =
        categoryDao.getAllOrderByAsc().map { rows -> rows.map { it.toDomain() } }

    override suspend fun insert(category: Category): Long =
        categoryDao.insert(category.toEntity())

    override suspend fun setActive(id: Long, active: Boolean) =
        categoryDao.setActive(id, active)
}
