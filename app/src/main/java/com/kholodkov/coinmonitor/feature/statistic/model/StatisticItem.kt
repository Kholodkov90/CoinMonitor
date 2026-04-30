package com.kholodkov.coinmonitor.feature.statistic.model

data class StatisticItem(
    val period: String,
    val totalSpent: String,
    val transactionCount: Int,
    val average: String
)