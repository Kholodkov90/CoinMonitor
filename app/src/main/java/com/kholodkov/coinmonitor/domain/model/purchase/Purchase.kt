package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class Purchase(
    val uid: String,
    val date: LocalDate,
    val userUid: String,
    val amount: BigDecimal,
    val transactionUid: String?,
    val currency: Currency,
    val description: String,
    val userName: String,
    val updatedAt: Instant
)

fun Purchase.toProjection(status: PurchaseStatus) = PurchaseProjection(
    uid = uid,
    date = date,
    userUid = userUid,
    amount = amount,
    transactionUid = transactionUid,
    currency = currency,
    description = description,
    userName = userName,
    status = status,
    updatedAt = updatedAt
)
