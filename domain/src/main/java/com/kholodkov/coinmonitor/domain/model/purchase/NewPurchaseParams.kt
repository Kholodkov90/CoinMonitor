package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class NewPurchaseParams(
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: Currency,
    val description: String
)