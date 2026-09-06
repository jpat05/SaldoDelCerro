package com.movil.saldodelcerro.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val password: String,
    val role: UserRole,
    val useBiometrics: Boolean = false
)
