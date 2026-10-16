package com.movil.saldodelcerro.data.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.movil.saldodelcerro.data.models.CategoryEntity
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.models.UserEntity
import com.movil.saldodelcerro.utils.Converters

@Database(
    entities = [TransactionEntity::class, UserEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionsDao(): TransactionsDao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "billetera_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
