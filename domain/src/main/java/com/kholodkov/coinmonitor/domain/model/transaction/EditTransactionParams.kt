package com.kholodkov.coinmonitor.domain.model.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalTime

data class EditTransactionParams(
    val uid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime
)