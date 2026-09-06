package com.movil.saldodelcerro.data.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movil.saldodelcerro.data.models.UserEntity
import com.movil.saldodelcerro.data.models.UserRole

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE role = :role LIMIT 1")
    suspend fun getUserByRole(role: UserRole): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)
}