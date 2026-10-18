package com.movil.saldodelcerro.domain.model

/**
 * A user-visible category. Categories are dynamic rows (users add/remove them), never a
 * hardcoded enum.
 *
 * No persistence concern crosses this boundary: the type carries no Room annotation, so the UI
 * layer compiles and previews without the database. `active` is a business rule (soft delete),
 * not a column detail — an archived category keeps its movements but vanishes from the pickers.
 */
data class Category(
    val id: Long = 0L,
    val name: String,
    val isIncome: Boolean,
    val active: Boolean = true
)
