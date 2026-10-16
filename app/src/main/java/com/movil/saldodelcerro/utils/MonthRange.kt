package com.movil.saldodelcerro.utils

import java.time.YearMonth
import java.time.ZoneId

/**
 * Milisegundos del primer instante del mes en curso, en hora local.
 * Se usa para los totales mensuales del Inicio.
 */
fun startOfCurrentMonthMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    YearMonth.now(zone)
        .atDay(1)
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
