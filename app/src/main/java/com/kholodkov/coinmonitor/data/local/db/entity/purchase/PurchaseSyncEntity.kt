package com.kholodkov.coinmonitor.data.local.db.entity.purchase

import androidx.room.ColumnInfo
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class PurchaseSyncEntity(
    @ColumnInfo(name = "uid")
    val uid: String,
    @ColumnInfo(name = "userUid")
    val userUid: String,
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "transactionUid")
    val transactionUid: String?,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Instant,
)