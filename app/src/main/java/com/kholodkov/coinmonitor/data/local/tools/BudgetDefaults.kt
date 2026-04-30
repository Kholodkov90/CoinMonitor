package com.kholodkov.coinmonitor.data.local.tools

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.LocalDate

// TODO: Replace hardcoded defaults with firebase remote config
object BudgetDefaults {
    val DAILY_LIMIT = BigDecimal("6000")
    val DEFAULT_CURRENCY = Currency.RSD
    val START_DATE: LocalDate = LocalDate.parse("2015-12-01")
}