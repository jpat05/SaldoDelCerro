package com.movil.saldodelcerro.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionDto(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "typeId",
        entityColumn = "id"
    ) val type: CategoryEntity
)