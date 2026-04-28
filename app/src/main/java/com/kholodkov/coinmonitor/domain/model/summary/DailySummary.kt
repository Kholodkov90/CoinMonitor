package com.kholodkov.coinmonitor.domain.model.summary

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

data class DailySummary(
    val budget: BigDecimal,
    val spent: BigDecimal,
    val remaining: BigDecimal,
    val currency: Currency
)