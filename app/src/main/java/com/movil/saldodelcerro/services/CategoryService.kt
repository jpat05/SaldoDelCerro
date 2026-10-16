package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.repo.CategoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Category layer. Categories are dynamic rows (users add/remove them), never a hardcoded enum.
 */
class CategoryService(
    private val categoryDao: CategoryDao
) {
    val categories: Flow<List<CategoryEntity>> = categoryDao.getAllOrderByAsc()

    val expenseCategories: Flow<List<CategoryEntity>> =
        categories.map { list -> list.filter { !it.isIncome } }

    val incomeCategories: Flow<List<CategoryEntity>> =
        categories.map { list -> list.filter { it.isIncome } }

    suspend fun add(category: CategoryEntity): Long = categoryDao.insert(category)

    suspend fun remove(category: CategoryEntity) = categoryDao.delete(category)
}
