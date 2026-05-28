package com.kholodkov.coinmonitor.data.model.transaction

import androidx.annotation.Keep
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Keep
data class RemoteTransaction(
    val uid: String,
    val date: LocalDate,
    val userUid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime,
    val updatedAt: Long
)