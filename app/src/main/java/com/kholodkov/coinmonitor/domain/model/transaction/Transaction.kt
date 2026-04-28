package com.kholodkov.coinmonitor.domain.model.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class Transaction(
    val uid: String,
    val date: LocalDate,
    val userUid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime,
    val updatedAt: Instant,
    val user: String
)
