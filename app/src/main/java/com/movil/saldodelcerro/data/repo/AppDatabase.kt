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

    abstract fun transactionsDao(): TransactionsDao
    abstract fun userDao(): UserDao
    abstract fun transactionTypeDao(): TransactionTypeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "billetera_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database.transactionTypeDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(transactionTypeDao: TransactionTypeDao) {
            val categorias = listOf(
                TransactionTypeEntity(name = "Alimentación", isIncome = false),
                TransactionTypeEntity(name = "Transporte", isIncome = false),
                TransactionTypeEntity(name = "Vivienda", isIncome = false),
                TransactionTypeEntity(name = "Salud", isIncome = false),
                TransactionTypeEntity(name = "Entretenimiento", isIncome = false),
                TransactionTypeEntity(name = "Educación", isIncome = false),
                TransactionTypeEntity(name = "Otros gastos", isIncome = false),
                TransactionTypeEntity(name = "Salario", isIncome = true),
                TransactionTypeEntity(name = "Freelance", isIncome = true),
                TransactionTypeEntity(name = "Otros ingresos", isIncome = true)
            )
            categorias.forEach { transactionTypeDao.insertType(it) }
        }
    }
}