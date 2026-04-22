package com.kholodkov.coinmonitor.core.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kholodkov.coinmonitor.core.navigation.Route
import com.kholodkov.coinmonitor.feature.login.screen.AuthScreenRoute

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        route = Route.AuthGraph.route,
        startDestination = Route.AuthScreen.route
    ) {
        composable(Route.AuthScreen.route) {
            AuthScreenRoute(
                onLoginSuccess = {
                    navController.navigate(Route.HomeGraph.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}