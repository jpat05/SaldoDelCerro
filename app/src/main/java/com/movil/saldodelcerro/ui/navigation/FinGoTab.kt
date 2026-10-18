package com.movil.saldodelcerro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.graphics.vector.ImageVector

internal enum class FinGoTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Filled.Home),
    Income("Capital", Icons.Filled.AccountBalanceWallet),
    Expenses("Egresos", Icons.Filled.RemoveCircle),
    Summary("Resumen", Icons.AutoMirrored.Filled.List)
}
