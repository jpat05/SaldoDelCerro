package com.movil.saldodelcerro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.movil.saldodelcerro.data.repo.AppDatabase
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.ui.navigation.FinGoApp

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
    private val walletService by lazy {
        WalletService(
            userDao = database.userDao(),
            transactionTypeDao = database.transactionTypeDao(),
            transactionDao = database.transactionsDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaldoDelCerroTheme {
                FinGoApp(walletService = walletService)
            }
        }
    }
}