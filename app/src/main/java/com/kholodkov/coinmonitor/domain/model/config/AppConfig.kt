package com.kholodkov.coinmonitor.domain.model.config

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

data class AppConfig(
    val dailyLimit: BigDecimal,
    val dailyLimitCurrency: Currency,
    val startDate: LocalDate,
    val initialBalance: BigDecimal
)