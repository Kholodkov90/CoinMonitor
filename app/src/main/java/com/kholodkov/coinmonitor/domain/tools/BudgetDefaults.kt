package com.kholodkov.coinmonitor.domain.tools

import java.math.BigDecimal

// TODO: Replace hardcoded defaults with firebase remote config
object BudgetDefaults {
    val DAILY_LIMIT = BigDecimal("6000")
    val DEFAULT_RATE = BigDecimal("117.2")
    const val START_DATE = "01.04.2026"
}