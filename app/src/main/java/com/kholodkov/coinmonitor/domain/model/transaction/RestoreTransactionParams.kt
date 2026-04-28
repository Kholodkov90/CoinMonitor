package com.kholodkov.coinmonitor.domain.model.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.Instant

data class RestoreTransactionParams(
    val date: LocalDate,
    val uid: String,
    val userUid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime,
    val updatedAt: Instant
)