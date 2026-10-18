package com.movil.saldodelcerro.domain.model

import java.math.BigDecimal

/** Total spent in one category, for the summary screen. */
data class CategoryTotal(
    val name: String,
    val total: BigDecimal
)
