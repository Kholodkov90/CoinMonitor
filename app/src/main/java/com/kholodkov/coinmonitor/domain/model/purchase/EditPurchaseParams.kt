package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class EditPurchaseParams(
    val uid: String,
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: Currency,
    val description: String
)