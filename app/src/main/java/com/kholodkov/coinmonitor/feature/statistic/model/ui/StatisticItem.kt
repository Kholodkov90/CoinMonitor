package com.kholodkov.coinmonitor.feature.statistic.model.ui

data class StatisticItem(
    val period: String,
    val totalSpent: String,
    val transactionCount: String,
    val average: String
)