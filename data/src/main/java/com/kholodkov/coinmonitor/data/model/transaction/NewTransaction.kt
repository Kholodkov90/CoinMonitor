package com.kholodkov.coinmonitor.data.model.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalTime

data class NewTransaction(
    val uid: String,
    val dayId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime,
    val updatedAt: Instant
)