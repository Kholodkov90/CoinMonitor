package com.kholodkov.coinmonitor.data.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class RemotePurchase (
    val uid: String,
    val date: LocalDate,
    val userUid: String,
    val amount: BigDecimal,
    val transactionUid: String?,
    val currency: Currency,
    val description: String,
    val updatedAt: Long
)