package com.movil.saldodelcerro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.movil.saldodelcerro.ui.navigation.FinGoApp
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SaldoDelCerroApplication

        setContent {
            SaldoDelCerroTheme {
                FinGoApp(
                    ledgerService = app.ledgerService,
                    categoryService = app.categoryService
                )
            }
        }
    }
}
