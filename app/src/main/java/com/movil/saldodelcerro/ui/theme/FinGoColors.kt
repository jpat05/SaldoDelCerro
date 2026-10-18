package com.movil.saldodelcerro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colores semanticos que MaterialColorScheme no cubre: ingresos/egresos y la
 * paleta decorativa de categorias. Se exponen via [MaterialTheme.finGo], igual
 * que los roles de Material, para que ningun composable declare literales.
 */
@Immutable
data class FinGoColors(
    val income: Color,
    val incomeContainer: Color,
    val expense: Color,
    val expenseContainer: Color,
    val categoryPalette: List<CategoryChipColors>
)

/** Par contenedor/contenido de un chip de categoria (fondo suave + acento). */
@Immutable
data class CategoryChipColors(
    val container: Color,
    val content: Color,
    val onContent: Color
)

/*
 * Tokens extra que la paleta de marca no define. Son privados: solo existen para
 * construir [LightFinGoColors] / [DarkFinGoColors] y no deben usarse fuera de aqui.
 */
private val Violet = Color(0xFF4A148C)
private val VioletSoft = Color(0xFFE8DEF8)
private val VioletDark = Color(0xFFD0BCFF)
private val VioletSoftDark = Color(0xFF2A1A47)

private val Amber = Color(0xFFE65100)
private val AmberSoft = Color(0xFFFFE0B2)
private val AmberDark = Color(0xFFFFB74D)
private val AmberSoftDark = Color(0xFF3A2410)

private val Neutral = Color(0xFF424242)
private val NeutralSoft = Color(0xFFE0E0E0)
private val NeutralDark = Color(0xFFBDBDBD)
private val NeutralSoftDark = Color(0xFF2A2E33)

private val Teal = Color(0xFF00796B)
private val TealSoft = Color(0xFFB2DFDB)
private val TealDark = Color(0xFF4DB6AC)
private val TealSoftDark = Color(0xFF13332E)

val LightFinGoColors = FinGoColors(
    income = FinGoGreen,
    incomeContainer = FinGoGreenSoft,
    expense = FinGoRed,
    expenseContainer = FinGoRedSoft,
    categoryPalette = listOf(
        CategoryChipColors(FinGoGreenSoft, FinGoGreen, FinGoBackground), // alimentacion
        CategoryChipColors(FinGoBlueSoft, FinGoBlue, FinGoBackground), // transporte
        CategoryChipColors(VioletSoft, Violet, FinGoBackground), // estudios
        CategoryChipColors(AmberSoft, Amber, FinGoBackground), // entretenimiento
        CategoryChipColors(FinGoRedSoft, FinGoRed, FinGoBackground), // salud
        CategoryChipColors(NeutralSoft, Neutral, FinGoBackground), // resto
        CategoryChipColors(TealSoft, Teal, FinGoBackground) // vivienda
    )
)

val DarkFinGoColors = FinGoColors(
    income = FinGoGreenDark,
    incomeContainer = FinGoGreenSoftDark,
    expense = FinGoRedDark,
    expenseContainer = FinGoRedSoftDark,
    categoryPalette = listOf(
        CategoryChipColors(FinGoGreenSoftDark, FinGoGreenDark, FinGoBackgroundDark),
        CategoryChipColors(FinGoBlueSoftDark, FinGoBlueDark, FinGoBackgroundDark),
        CategoryChipColors(VioletSoftDark, VioletDark, FinGoBackgroundDark),
        CategoryChipColors(AmberSoftDark, AmberDark, FinGoBackgroundDark),
        CategoryChipColors(FinGoRedSoftDark, FinGoRedDark, FinGoBackgroundDark),
        CategoryChipColors(NeutralSoftDark, NeutralDark, FinGoBackgroundDark),
        CategoryChipColors(TealSoftDark, TealDark, FinGoBackgroundDark)
    )
)

internal val LocalFinGoColors = staticCompositionLocalOf { LightFinGoColors }

/** Punto de acceso a los colores semanticos FinGO, al estilo de `MaterialTheme.colorScheme`. */
val MaterialTheme.finGo: FinGoColors
    @Composable
    @ReadOnlyComposable
    get() = LocalFinGoColors.current
