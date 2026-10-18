package com.movil.saldodelcerro.services

import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.domain.repository.CategoryRepository
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
    private val categoryRepository: CategoryRepository
) {
    /** Every row, archived included — only the service decides what is user-visible. */
    val allCategories: Flow<List<Category>> = categoryRepository.observeAll()

    val categories: Flow<List<Category>> =
        allCategories.map { list -> list.filter { it.active } }

    val expenseCategories: Flow<List<Category>> =
        categories.map { list -> list.filter { !it.isIncome } }

    val incomeCategories: Flow<List<Category>> =
        categories.map { list -> list.filter { it.isIncome } }

    /**
     * Adds a category, matching names case- and accent-insensitively ([normalizedKey]).
     * An archived match is reactivated instead of duplicated; an active match is reused as-is.
     *
     * @return the id of the created / reused category, or `null` when the name is blank.
     */
    suspend fun add(name: String, isIncome: Boolean): Long? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null

        val key = trimmed.normalizedKey()
        val existing = allCategories.first().find { it.name.normalizedKey() == key }
        if (existing != null) {
            if (!existing.active) categoryRepository.setActive(existing.id, true)
            return existing.id
        }

        return categoryRepository.insert(Category(name = trimmed, isIncome = isIncome))
    }

    suspend fun archive(category: Category) = categoryRepository.setActive(category.id, false)

    suspend fun restore(category: Category) = categoryRepository.setActive(category.id, true)
}
