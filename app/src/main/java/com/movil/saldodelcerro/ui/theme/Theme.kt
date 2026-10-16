package com.movil.saldodelcerro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = FinGoBlueDark,
    onPrimary = FinGoBlueSoftDark,
    primaryContainer = FinGoBlueDark,
    onPrimaryContainer = FinGoBlueSoftDark,
    secondary = FinGoMutedDark,
    onSecondary = FinGoBackgroundDark,
    tertiary = FinGoGreenDark,
    onTertiary = FinGoGreenSoftDark,
    error = FinGoRedDark,
    onError = FinGoRedSoftDark,
    background = FinGoBackgroundDark,
    onBackground = FinGoInkDark,
    surface = FinGoBackgroundDark,
    onSurface = FinGoInkDark,
    surfaceContainerLow = FinGoSurfaceDark,
    surfaceContainer = FinGoSurfaceDark,
    surfaceContainerHigh = FinGoSurfaceDark,
    outline = FinGoOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = FinGoBlue,
    onPrimary = FinGoBackground,
    primaryContainer = FinGoBlueSoft,
    onPrimaryContainer = FinGoInk,
    secondary = FinGoMuted,
    onSecondary = FinGoBackground,
    tertiary = FinGoGreen,
    onTertiary = FinGoBackground,
    error = FinGoRed,
    onError = FinGoBackground,
    background = FinGoBackground,
    onBackground = FinGoInk,
    surface = FinGoBackground,
    onSurface = FinGoInk,
    surfaceContainerLow = FinGoSurface,
    surfaceContainer = FinGoSurface,
    surfaceContainerHigh = FinGoSurface,
    outline = FinGoOutline
)

@Composable
fun SaldoDelCerroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // FinGO tiene paleta propia: el color dinamico del sistema se deja desactivado
    // para que la app se vea siempre igual.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FinGoShapes,
        content = content
    )
}
