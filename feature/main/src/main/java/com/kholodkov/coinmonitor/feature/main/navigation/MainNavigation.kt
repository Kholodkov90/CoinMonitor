package com.kholodkov.coinmonitor.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kholodkov.coinmonitor.feature.main.screen.MainScreenRoute

fun NavGraphBuilder.mainScreen() {
    composable(MainRoute.ROUTE) {
        MainScreenRoute()
    }
}

object MainRoute {
    const val ROUTE = "main_screen"
}