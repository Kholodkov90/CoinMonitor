package com.kholodkov.coinmonitor.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kholodkov.coinmonitor.app.navigation.AppGraph
import com.kholodkov.coinmonitor.app.navigation.AppNavHost
import com.kholodkov.coinmonitor.app.navigation.BottomBar
import com.kholodkov.coinmonitor.core.ui.theme.CoinMonitorTheme

@Composable
fun AppRoot(isLoggedIn: Boolean) {
    val startDestination = if (isLoggedIn) AppGraph.HOME else AppGraph.AUTH

    CoinMonitorTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val isBottomBarVisible = navBackStackEntry
            ?.destination
            ?.hierarchy
            ?.any { it.route == AppGraph.HOME } == true

        Scaffold(
            bottomBar = { if (isBottomBarVisible) BottomBar(navController) }
        ) { padding ->
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            )
        }
    }
}