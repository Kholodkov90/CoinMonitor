package com.kholodkov.coinmonitor.feature.statistic.state

import com.kholodkov.coinmonitor.feature.statistic.model.ui.StatisticItem

data class StatisticUiState(
    val weekItems: List<StatisticItem> = listOf(),
    val monthItems: List<StatisticItem> = listOf(),
    val yearItems: List<StatisticItem> = listOf()
)