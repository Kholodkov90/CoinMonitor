package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class SavePurchaseParams(
    val uid: String?,
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: Currency,
    val description: String,
)

fun SavePurchaseParams.toNewPurchaseParams() = NewPurchaseParams(
    date = date,
    amount = amount,
    currency = currency,
    description = description
)

fun SavePurchaseParams.toEditPurchaseParams() = EditPurchaseParams(
    uid = uid ?: error("uid mustn't be null"),
    date = date,
    amount = amount,
    currency = currency,
    description = description,
)