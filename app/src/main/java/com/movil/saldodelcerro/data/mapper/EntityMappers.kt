package com.movil.saldodelcerro.data.mapper

import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.TransactionDto
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.domain.model.Category
import com.movil.saldodelcerro.domain.model.Movement

/**
 * The single point where persistence types become domain types, and back.
 *
 * Keeping every conversion in this one file is what lets `services/` and `ui/` stay free of Room:
 * a Room-annotated type never travels in the direction of the UI.
 */

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    isIncome = isIncome,
    active = active
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    isIncome = isIncome,
    active = active
)

/** Flattens the `@Relation` so callers never walk `dto.transaction` / `dto.type`. */
fun TransactionDto.toDomain(): Movement = Movement(
    id = transaction.id,
    reason = transaction.reason,
    amount = transaction.amount,
    date = transaction.generatedDate,
    categoryId = transaction.typeId,
    categoryName = type.name,
    isIncome = type.isIncome,
    categoryActive = type.active
)

fun Movement.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    reason = reason,
    amount = amount,
    typeId = categoryId,
    generatedDate = date
)
