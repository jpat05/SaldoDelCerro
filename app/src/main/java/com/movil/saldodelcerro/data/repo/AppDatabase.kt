package com.movil.saldodelcerro.data.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.movil.saldodelcerro.data.models.TransactionEntity
import com.movil.saldodelcerro.data.models.TransactionTypeEntity
import com.movil.saldodelcerro.data.models.UserEntity
import com.movil.saldodelcerro.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, UserEntity::class, TransactionTypeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionsDao
    abstract fun userDao(): UserDao
    abstract fun transactionTypeDao(): TransactionTypeDao

    companion object {
        private val defaultExpenseTypes = listOf("Comida", "Transporte", "Ocio", "Vivienda", "Salud", "Otros")

        private val seedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { instance ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (instance.transactionTypeDao().count() == 0) {
                            defaultExpenseTypes.forEach { name ->
                                instance.transactionTypeDao().insertType(
                                    TransactionTypeEntity(name = name, isIncome = false)
                                )
                            }
                        }
                    }
                }
            }
        }

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "billetera_db"
                ).addCallback(seedCallback).build()
                INSTANCE = instance
                instance
            }
        }
    }
}