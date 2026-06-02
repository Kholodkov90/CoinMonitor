package com.kholodkov.coinmonitor.data.local.db.entity.purchase

import androidx.room.ColumnInfo
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class PlannedPurchaseEntity(
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "date")
    val date: LocalDate,
)