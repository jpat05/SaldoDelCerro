package com.movil.saldodelcerro.utils

private const val DECIMAL_POINT = '.'
private const val MAX_DECIMALS = 2

/**
 * Deja pasar solo digitos y un separador decimal, con maximo [MAX_DECIMALS]
 * decimales. Acepta coma o punto (teclados en espanol) y normaliza a punto,
 * que es como los formatea [formatMoney].
 *
 * Es el filtro de los campos de monto (egresos y capital), que antes se
 * limitaban a enteros.
 */
fun String.filterAmountInput(): String {
    val cleaned = filter { it.isDigit() || it == DECIMAL_POINT || it == ',' }
        .replace(',', DECIMAL_POINT)
    val point = cleaned.indexOf(DECIMAL_POINT)
    if (point < 0) return cleaned

    val integerPart = cleaned.substring(0, point + 1)
    val decimalPart = cleaned.substring(point + 1)
        .filter { it != DECIMAL_POINT }
        .take(MAX_DECIMALS)
    return integerPart + decimalPart
}
