package com.kholodkov.coinmonitor.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kholodkov.coinmonitor.core.navigation.BottomBar
import com.kholodkov.coinmonitor.core.navigation.Route
import com.kholodkov.coinmonitor.core.navigation.graph.authGraph
import com.kholodkov.coinmonitor.core.navigation.graph.homeGraph
import com.kholodkov.coinmonitor.core.ui.theme.CoinMonitorTheme

@Composable
fun AppRoot(isLoggedIn: Boolean) {
    val startDestination =
        if (isLoggedIn) Route.HomeGraph.route
        else Route.AuthGraph.route

    CoinMonitorTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val isBottomBarVisible = navBackStackEntry
            ?.destination
            ?.hierarchy
            ?.any { it.route == Route.HomeGraph.route } == true

        Scaffold(
            bottomBar = { if (isBottomBarVisible) BottomBar(navController) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            ) {
                authGraph(navController)
                homeGraph(navController)
            }
        }
    }
}