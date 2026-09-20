package com.movil.saldodelcerro.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.formatMoney(): String =
    NumberFormat.getCurrencyInstance(Locale("es", "EC")).format(this)