package com.movil.saldodelcerro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.movil.saldodelcerro.data.repo.AppDatabase
import com.movil.saldodelcerro.services.CategoryService
import com.movil.saldodelcerro.services.LedgerService
import com.movil.saldodelcerro.services.TransactionService
import com.movil.saldodelcerro.ui.navigation.FinGoApp
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }

    private val transactionService by lazy { TransactionService(database.transactionsDao()) }
    private val categoryService by lazy { CategoryService(database.categoryDao()) }
    private val ledgerService by lazy { LedgerService(transactionService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaldoDelCerroTheme {
                FinGoApp(
                    transactionService = transactionService,
                    categoryService = categoryService,
                    ledgerService = ledgerService
                )
            }
        }
    }
}
