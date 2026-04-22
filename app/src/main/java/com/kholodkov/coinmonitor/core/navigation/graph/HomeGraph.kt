package com.kholodkov.coinmonitor.core.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kholodkov.coinmonitor.core.navigation.Route
import com.kholodkov.coinmonitor.feature.main.screen.MainScreenRoute
import com.kholodkov.coinmonitor.feature.purchase.PurchaseScreenRoute
import com.kholodkov.coinmonitor.feature.settings.ui.screen.SettingsScreenRoute
import com.kholodkov.coinmonitor.feature.statistic.StatisticScreenRoute

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    navigation(
        route = Route.HomeGraph.route,
        startDestination = Route.MainScreen.route
    ) {
        composable(Route.MainScreen.route) { MainScreenRoute() }
        composable(Route.PurchaseScreen.route) { PurchaseScreenRoute() }
        composable(Route.StatisticScreen.route) { StatisticScreenRoute() }
        composable(Route.SettingsScreen.route) {
            SettingsScreenRoute(
                onSignOut = {
                    navController.navigate(Route.AuthGraph.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}