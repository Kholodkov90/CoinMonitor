package com.kholodkov.coinmonitor.data.local.db.entity.day

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity(
    tableName = "day",
    indices = [
        Index("day_date", unique = true)
    ]
)
data class DayEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "day_id")
    val id: Long = 0,
    @ColumnInfo(name = "day_date")
    val date: LocalDate,
    @ColumnInfo(name = "day_exchange_rate")
    val exchangeRate: BigDecimal? = null
)