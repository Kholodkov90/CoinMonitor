package com.kholodkov.coinmonitor.data.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class PlannedPurchase(
    val amount: BigDecimal,
    val currency: Currency,
    val date: LocalDate
)