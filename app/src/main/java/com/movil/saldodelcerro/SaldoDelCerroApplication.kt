package com.movil.saldodelcerro

import android.app.Application
import com.movil.saldodelcerro.data.repo.AppDatabase
import com.movil.saldodelcerro.data.seed.CategorySeeder
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.services.TransactionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Composition root: the analog of a Spring application context.
 *
 * The services are process-wide singletons (same lifetime as the Room database), so retained
 * ViewModels never end up holding stale instances across Activity re-creation. Seeding lives here
 * too, because it is an application-startup concern, not a schema-creation one.
 */
class SaldoDelCerroApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this) }
    val transactionService by lazy { TransactionService(database.transactionsDao()) }
    val categoryService by lazy { CategoryService(database.categoryDao()) }
    val ledgerService by lazy { LedgerService(transactionService, categoryService) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { CategorySeeder.seed(database.categoryDao()) }
    }
}
