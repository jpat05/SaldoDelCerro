package com.movil.saldodelcerro.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryStyle(
    val tintColor: Color,
    val backgroundColor: Color,
    val textColor: Color,
    val icon: ImageVector
)

fun getCategoryStyle(categoryName: String): CategoryStyle {
    return when (categoryName.lowercase().trim()) {
        "comida", "alimentos", "restaurante" -> CategoryStyle(
            tintColor = Color(0xFFE67E53),
            backgroundColor = Color(0xFFFDEEDC),
            textColor = Color(0xFF8A3B14),
            icon = Icons.Default.Fastfood
        )
        "hogar", "casa", "renta" -> CategoryStyle(
            tintColor = Color(0xFF3498DB),
            backgroundColor = Color(0xFFEBF5FB),
            textColor = Color(0xFF1B4F72),
            icon = Icons.Default.Home
        )
        "compras", "ropa", "supermercado" -> CategoryStyle(
            tintColor = Color(0xFF9B59B6),
            backgroundColor = Color(0xFFF4ECF7),
            textColor = Color(0xFF512E5F),
            icon = Icons.Default.LocalMall
        )
        "transporte", "auto", "gasolina" -> CategoryStyle(
            tintColor = Color(0xFF1ABC9C),
            backgroundColor = Color(0xFFE8F8F5),
            textColor = Color(0xFF0E6251),
            icon = Icons.Default.DirectionsCar
        )
        else -> CategoryStyle(
            tintColor = Color(0xFF7F8C8D),
            backgroundColor = Color(0xFFF2F4F4),
            textColor = Color(0xFF2C3E50),
            icon = Icons.Default.Category
        )
    }
}