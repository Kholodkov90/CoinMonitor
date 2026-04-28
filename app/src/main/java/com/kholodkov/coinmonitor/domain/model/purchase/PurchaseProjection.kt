package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Instant

data class PurchaseProjection(
    val uid: String,
    val date: LocalDate,
    val userUid: String,
    val amount: BigDecimal,
    val transactionUid: String?,
    val currency: Currency,
    val description: String,
    val userName: String,
    val status: PurchaseStatus,
    val updatedAt: Instant
)
