package com.movil.saldodelcerro.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** Simbolo de moneda de la app. */
const val CURRENCY_SYMBOL = "$"

// Separador de miles ',' y decimal '.' fijo, sin depender del locale del equipo.
private val amountFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

/** Formatea un monto como `$ 1,234.56`. */
fun BigDecimal.formatMoney(): String = "$CURRENCY_SYMBOL ${amountFormat.format(this)}"
