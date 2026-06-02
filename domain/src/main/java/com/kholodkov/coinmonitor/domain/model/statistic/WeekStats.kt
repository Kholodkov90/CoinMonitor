package com.kholodkov.coinmonitor.domain.model.statistic

import java.math.BigDecimal
import java.time.LocalDate

data class WeekStats(
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val transactionCount: Int,
    val totalSpent: BigDecimal,
    val average: BigDecimal
)