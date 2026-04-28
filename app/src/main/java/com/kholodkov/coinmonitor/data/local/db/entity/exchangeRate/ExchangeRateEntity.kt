package com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

@Entity(
    tableName = "exchange_rate",
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["day_id"],
            childColumns = ["exr_day_id"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = ["exr_day_id", "exr_currency"], unique = true)
    ]
)
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exr_id")
    val id: Long = 0,
    @ColumnInfo(name = "exr_day_id")
    val dayId: Long,
    @ColumnInfo(name = "exr_currency")
    val currency: Currency,
    @ColumnInfo(name = "exr_rate")
    val exchangeRate: BigDecimal
)
