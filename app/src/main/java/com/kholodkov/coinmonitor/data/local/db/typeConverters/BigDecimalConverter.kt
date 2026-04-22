package com.kholodkov.coinmonitor.data.local.db.typeConverters

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalConverter {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal) = value
        .movePointRight(2)
        .longValueExact()

    @TypeConverter
    fun toBigDecimal(value: Long) = BigDecimal(value)
        .movePointLeft(2)
}