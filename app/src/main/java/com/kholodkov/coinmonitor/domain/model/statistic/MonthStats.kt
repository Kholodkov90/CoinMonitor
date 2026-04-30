package com.kholodkov.coinmonitor.domain.model.statistic

import java.math.BigDecimal
import java.time.LocalDate

data class MonthStats(
    val monthStart: LocalDate,
    val transactionCount: Int,
    val totalSpent: BigDecimal,
    val average: BigDecimal
)