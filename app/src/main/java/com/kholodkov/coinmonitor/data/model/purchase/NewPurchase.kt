package com.kholodkov.coinmonitor.data.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant

data class NewPurchase(
    val uid: String,
    val dayId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val transactionId: Long?,
    val currency: Currency,
    val description: String,
    val updatedAt: Instant
)