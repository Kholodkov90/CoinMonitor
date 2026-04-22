package com.kholodkov.coinmonitor.domain.model

import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

data class DailySummary(
    val balance: BigDecimal,
    val spent: BigDecimal,
    val remaining : BigDecimal,
    val currency: Currency
)