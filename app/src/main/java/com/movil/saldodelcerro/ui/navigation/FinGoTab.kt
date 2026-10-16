package com.movil.saldodelcerro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

internal enum class FinGoTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Filled.Home),
    Capital("Capital", Icons.Filled.Star),
    Expenses("Egresos", Icons.Filled.AddCircle),
    Summary("Resumen", Icons.Filled.List)
}
