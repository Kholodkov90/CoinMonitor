package com.kholodkov.coinmonitor.domain.model.statistic

import com.kholodkov.coinmonitor.domain.model.currency.Currency

data class StatisticSummary (
    val currency: Currency,
    val weeklyStats: List<WeekStats>,
    val monthlyStats: List<MonthStats>,
    val yearlyStats: List<YearStats>,
)