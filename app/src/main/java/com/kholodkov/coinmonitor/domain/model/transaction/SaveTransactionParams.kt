package com.kholodkov.coinmonitor.domain.model.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

data class SaveTransactionParams(
    val uid: String?,
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime,
)

fun SaveTransactionParams.toNewTransactionParams() = NewTransactionParams(
    date = date,
    amount = amount,
    currency = currency,
    time = time
)

fun SaveTransactionParams.toEditTransactionParams() = EditTransactionParams(
    uid = uid ?: error("uid mustn't be null"),
    amount = amount,
    currency = currency,
    time = time
)