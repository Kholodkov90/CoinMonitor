package com.kholodkov.coinmonitor.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import com.kholodkov.coinmonitor.feature.login.navigation.LoginRoute
import com.kholodkov.coinmonitor.feature.login.navigation.loginScreen
import com.kholodkov.coinmonitor.feature.main.navigation.MainRoute
import com.kholodkov.coinmonitor.feature.main.navigation.mainScreen
import com.kholodkov.coinmonitor.feature.purchase.navigation.purchasesScreen
import com.kholodkov.coinmonitor.feature.settings.navigation.settingsScreen
import com.kholodkov.coinmonitor.feature.statistic.navigation.statisticScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(route = AppGraph.AUTH, startDestination = LoginRoute.ROUTE) {
            loginScreen(
                onLoginSuccess = {
                    navController.navigate(AppGraph.HOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        navigation(route = AppGraph.HOME, startDestination = MainRoute.ROUTE) {
            mainScreen()
            purchasesScreen()
            statisticScreen()
            settingsScreen(
                onSignOut = {
                    navController.navigate(AppGraph.AUTH) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}