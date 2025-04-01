package com.bytesdrawer.budgetplanner.common.utils

import androidx.room.TypeConverter
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String {
        return value.toString()
    }

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal {
        return value.toBigDecimal()
    }
}