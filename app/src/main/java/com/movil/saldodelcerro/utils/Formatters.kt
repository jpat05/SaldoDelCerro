package com.movil.saldodelcerro.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

private val formatoMoneda: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "EC")).apply {
    maximumFractionDigits = 2
    minimumFractionDigits = 2
}

private val formatoFecha: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "EC"))

fun BigDecimal.formatMoney(): String = formatoMoneda.format(this)

fun Long.formatoFecha(): String = formatoFecha.format(Date(this))

/**
 * Keeps only what can form a money value: digits plus a single decimal separator, which is
 * normalized to `.` (the app's locale, es-EC, shows the decimal separator as a comma).
 * Decimals are capped at two so `20,509` cannot become `$20,509`.
 */
fun String.sanitizeMoneyInput(): String {
    val normalized = filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
    val dot = normalized.indexOf('.')
    if (dot == -1) return normalized
    val integerPart = normalized.substring(0, dot)
    val decimals = normalized.substring(dot + 1).filter(Char::isDigit).take(2)
    return "$integerPart.$decimals"
}

/** [sanitizeMoneyInput] + parse, rejecting blank, zero and non-numeric input. */
fun String.toMoneyOrNull(): BigDecimal? =
    sanitizeMoneyInput().toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }