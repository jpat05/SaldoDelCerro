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

fun String.limpiarMonto(): String = this.filter(Char::isDigit)