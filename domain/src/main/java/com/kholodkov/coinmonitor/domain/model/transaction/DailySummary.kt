package com.kholodkov.coinmonitor.domain.model.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

data class DailySummary(
    val budget: BigDecimal,
    val spent: BigDecimal,
    val remaining: BigDecimal,
    val currency: Currency
)