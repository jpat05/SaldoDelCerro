package com.movil.saldodelcerro.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryStyle(
    val icon: ImageVector,
    val backgroundColor: Color,
    val tintColor: Color,
    val textColor: Color = Color(0xFF1C1B1F) // Color oscuro garantizado para Modo Oscuro
)

fun getCategoryStyle(categoryName: String): CategoryStyle {
    return when (categoryName.lowercase().trim()) {
        "alimentación", "alimentacion", "comida" -> CategoryStyle(
            icon = Icons.Default.Restaurant,
            backgroundColor = Color(0xFFD1F2D9),
            tintColor = Color(0xFF1B5E20)
        )
        "transporte" -> CategoryStyle(
            icon = Icons.Default.DirectionsCar,
            backgroundColor = Color(0xFFD6E4FF),
            tintColor = Color(0xFF0D47A1)
        )
        "estudios", "educación", "educacion" -> CategoryStyle(
            icon = Icons.Default.School,
            backgroundColor = Color(0xFFE8DEF8),
            tintColor = Color(0xFF4A148C)
        )
        "entretenimiento", "ocio" -> CategoryStyle(
            icon = Icons.Default.SportsEsports,
            backgroundColor = Color(0xFFFFE0B2),
            tintColor = Color(0xFFE65100)
        )
        "salud" -> CategoryStyle(
            icon = Icons.Default.Favorite,
            backgroundColor = Color(0xFFFFCDD2),
            tintColor = Color(0xFFB71C1C)
        )
        else -> CategoryStyle(
            icon = Icons.Default.MoreHoriz,
            backgroundColor = Color(0xFFE0E0E0),
            tintColor = Color(0xFF424242)
        )
    }
}