package com.kholodkov.coinmonitor.domain.model.statistic

import java.math.BigDecimal

data class YearStats(
    val year: Int,
    val transactionCount: Int,
    val totalSpent: BigDecimal,
    val average: BigDecimal
)