package com.kholodkov.coinmonitor.data.local.db.typeConverters

import androidx.room.TypeConverter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(value: LocalTime): Long = value.toSecondOfDay().toLong()

    @TypeConverter
    fun toLocalTime(value: Long): LocalTime = LocalTime.ofSecondOfDay(value)
}