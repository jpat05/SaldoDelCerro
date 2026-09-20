package com.movil.saldodelcerro.utils

import androidx.room.TypeConverter
import com.movil.saldodelcerro.data.models.UserRole
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class Converters {

    fun BigDecimal.formatMoney(): String =
        NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(this)
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromUserRole(role: UserRole?): String? = role?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? = value?.let { UserRole.valueOf(it) }
}