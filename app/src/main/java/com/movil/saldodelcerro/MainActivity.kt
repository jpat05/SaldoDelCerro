package com.movil.saldodelcerro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.movil.saldodelcerro.data.repo.AppDatabase
import com.movil.saldodelcerro.services.WalletService
import com.movil.saldodelcerro.ui.theme.SaldoDelCerroTheme
import com.movil.saldodelcerro.views.FinGoApp

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
