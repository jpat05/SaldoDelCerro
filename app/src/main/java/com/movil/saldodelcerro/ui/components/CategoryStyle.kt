package com.movil.saldodelcerro.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.movil.saldodelcerro.ui.theme.finGo

data class CategoryStyle(
    val icon: ImageVector,
    val backgroundColor: Color,
    val tintColor: Color,
    val textColor: Color,
    val onTintColor: Color
)

/**
 * Estilo visual de una categoria. Los colores salen del tema (paleta FinGO),
 * no de literales locales, y por eso la funcion es @Composable.
 */
@Composable
fun getCategoryStyle(categoryName: String): CategoryStyle {
    val key = categoryName.lowercase().trim()
    val palette = MaterialTheme.finGo.categoryPalette

    val (icon, chip) = when (key) {
        "alimentación", "alimentacion", "comida" ->
            Icons.Default.Restaurant to palette[0]
        "transporte" ->
            Icons.Default.DirectionsCar to palette[1]
        "estudios", "educación", "educacion" ->
            Icons.Default.School to palette[2]
        "entretenimiento", "ocio" ->
            Icons.Default.SportsEsports to palette[3]
        "salud" ->
            Icons.Default.Favorite to palette[4]
        else ->
            Icons.Default.MoreHoriz to palette[5]
    }

    return CategoryStyle(
        icon = icon,
        backgroundColor = chip.container,
        tintColor = chip.content,
        textColor = MaterialTheme.colorScheme.onSurface,
        onTintColor = chip.onContent
    )
}
