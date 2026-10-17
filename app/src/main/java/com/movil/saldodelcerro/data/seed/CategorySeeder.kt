package com.movil.saldodelcerro.data.seed

import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.repo.CategoryDao

/**
 * Bootstrap data for a fresh database.
 *
 * Deliberately kept out of [com.movil.saldodelcerro.data.repo.AppDatabase]: seeding is not schema
 * creation. The caller (startup / DI) owns invoking this.
 *
 * [seed] is idempotent: it does nothing if any category already exists.
 */
object CategorySeeder {

    val defaultCategories: List<CategoryEntity> = listOf(
        CategoryEntity(name = "Alimentación", isIncome = false),
        CategoryEntity(name = "Transporte", isIncome = false),
        CategoryEntity(name = "Vivienda", isIncome = false),
        CategoryEntity(name = "Salud", isIncome = false),
        CategoryEntity(name = "Entretenimiento", isIncome = false),
        CategoryEntity(name = "Educación", isIncome = false),
        CategoryEntity(name = "Otros gastos", isIncome = false),
        CategoryEntity(name = "Salario", isIncome = true),
        CategoryEntity(name = "Freelance", isIncome = true),
        CategoryEntity(name = "Otros ingresos", isIncome = true)
    )

    suspend fun seed(categoryDao: CategoryDao) {
        if (categoryDao.count() > 0) return
        defaultCategories.forEach { categoryDao.insert(it) }
    }
}
