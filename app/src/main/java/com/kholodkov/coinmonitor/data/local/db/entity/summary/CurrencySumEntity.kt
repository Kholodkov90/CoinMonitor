package com.kholodkov.coinmonitor.data.local.db.entity.summary

import androidx.room.ColumnInfo
import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

data class CurrencySumEntity(
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal
)