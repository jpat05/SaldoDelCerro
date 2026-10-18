package com.movil.saldodelcerro

import android.app.Application
import com.movil.saldodelcerro.data.repo.AppDatabase
import com.movil.saldodelcerro.data.repo.CategoryRepositoryImpl
import com.movil.saldodelcerro.data.repo.MovementRepositoryImpl
import com.movil.saldodelcerro.data.seed.CategorySeeder
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Composition root: the analog of a Spring application context.
 *
 * Wiring is layered — DAO → repository → service — so a dependency can only point one way. The
 * repositories are process-wide singletons (same lifetime as the Room database), so retained
 * ViewModels never end up holding stale instances across Activity re-creation. Seeding lives here
 * too, because it is an application-startup concern, not a schema-creation one.
 */
class SaldoDelCerroApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this) }

    // Data layer — the only place that knows Room exists.
    val categoryRepository by lazy { CategoryRepositoryImpl(database.categoryDao()) }
    val movementRepository by lazy { MovementRepositoryImpl(database.transactionsDao()) }

    // Domain layer — expressed purely in domain types.
    val categoryService by lazy { CategoryService(categoryRepository) }
    val ledgerService by lazy { LedgerService(movementRepository, categoryService) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { CategorySeeder.seed(database.categoryDao()) }
    }
}
