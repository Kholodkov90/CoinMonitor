package com.kholodkov.coinmonitor.data.local.db.entity.purchase

import androidx.room.ColumnInfo
import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class FullPurchaseEntity(
    @ColumnInfo(name = "uid")
    val uid: String,
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "userName")
    val userName: String
)