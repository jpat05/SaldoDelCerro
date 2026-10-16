package com.movil.saldodelcerro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.graphics.vector.ImageVector

internal enum class FinGoTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Filled.Home),
    Income("Ingresos", Icons.Filled.AddCircle),
    Expenses("Egresos", Icons.Filled.RemoveCircle),
    Summary("Resumen", Icons.Filled.List)
}
