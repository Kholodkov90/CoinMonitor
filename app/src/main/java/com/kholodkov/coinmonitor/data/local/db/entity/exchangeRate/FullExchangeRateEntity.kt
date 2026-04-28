package com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate

import androidx.room.ColumnInfo
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class FullExchangeRateEntity(
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "exchangeRate")
    val exchangeRate: BigDecimal
)
