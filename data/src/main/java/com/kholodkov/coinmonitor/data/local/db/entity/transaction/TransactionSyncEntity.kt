package com.kholodkov.coinmonitor.data.local.db.entity.transaction

import androidx.room.ColumnInfo
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class TransactionSyncEntity(
    @ColumnInfo(name = "uid")
    val uid: String,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "time")
    val time: LocalTime,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Instant,
    @ColumnInfo(name = "userUid")
    val userUid: String,
    @ColumnInfo(name = "date")
    val date: LocalDate
)