package com.movil.saldodelcerro.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SkinPrimary,
    onPrimary = TextOnSkin,
    primaryContainer = SkinContainer,
    onPrimaryContainer = SkinLight,
    secondary = SkinLight,
    onSecondary = TextOnSkin,
    background = NavyDark,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyContainer,
    onSurfaceVariant = TextSecondary
)

@Composable
fun SaldoDelCerroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}