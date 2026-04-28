package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class RestorePurchaseParams(
    val uid: String,
    val userUid: String,
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: Currency,
    val transactionUid: String?,
    val description: String,
    val updatedAt: Instant
)