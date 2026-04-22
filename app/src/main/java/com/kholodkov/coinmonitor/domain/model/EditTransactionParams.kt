package com.kholodkov.coinmonitor.domain.model

import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal
import java.time.LocalTime

data class EditTransactionParams(
    val uid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val time: LocalTime
)