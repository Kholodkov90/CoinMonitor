package com.kholodkov.coinmonitor.feature.statistic.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kholodkov.coinmonitor.feature.statistic.screen.StatisticScreenRoute

fun NavGraphBuilder.statisticScreen() {
    composable(StatisticRoute.ROUTE) {
        StatisticScreenRoute()
    }
}

object StatisticRoute {
    const val ROUTE = "statistic_screen"
}