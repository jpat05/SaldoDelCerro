package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.repo.CategoryDao
import com.movil.saldodelcerro.utils.normalizedKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Category layer. Categories are dynamic rows (users add/remove them), never a hardcoded enum.
 *
 * Archiving is a **soft delete**: the row stays so its movements keep resolving, but it drops out
 * of every user-facing list (`categories` / `expenseCategories` / `incomeCategories` are active-only).
 */
class CategoryService(
    private val categoryDao: CategoryDao
) {
    /** Every row, archived included — only the service decides what is user-visible. */
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllOrderByAsc()

    val categories: Flow<List<CategoryEntity>> =
        allCategories.map { list -> list.filter { it.active } }

    val expenseCategories: Flow<List<CategoryEntity>> =
        categories.map { list -> list.filter { !it.isIncome } }

    val incomeCategories: Flow<List<CategoryEntity>> =
        categories.map { list -> list.filter { it.isIncome } }

    /**
     * Adds a category, matching names case- and accent-insensitively ([normalizedKey]).
     * An archived match is reactivated instead of duplicated; an active match is reused as-is.
     *
     * @return the id of the created / reused category, or `null` when the name is blank.
     */
    suspend fun add(category: CategoryEntity): Long? {
        val name = category.name.trim()
        if (name.isEmpty()) return null

        val key = name.normalizedKey()
        val existing = allCategories.first().find { it.name.normalizedKey() == key }
        if (existing != null) {
            if (!existing.active) categoryDao.setActive(existing.id, true)
            return existing.id
        }

        return categoryDao.insert(category.copy(name = name, active = true))
    }

    suspend fun archive(category: CategoryEntity) = categoryDao.setActive(category.id, false)

    suspend fun restore(category: CategoryEntity) = categoryDao.setActive(category.id, true)
}
