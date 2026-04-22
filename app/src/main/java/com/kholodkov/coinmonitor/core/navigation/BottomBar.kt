package com.kholodkov.coinmonitor.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar {
        bottomBarItems.forEach { item ->
            val isSameItem = backStackEntry
                ?.destination
                ?.hierarchy
                ?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSameItem,
                onClick = {
                    if (isSameItem) return@NavigationBarItem
                    navController.navigate(item.route) {
                        popUpTo(Route.HomeGraph.route)
                    }
                },
                icon = { Icon(item.icon, null) },
                label = { Text(item.title) }
            )
        }
    }
}

private val bottomBarItems = listOf(
    BottomBarItem(Route.MainScreen.route, "Main", Icons.Default.Wallet),
    BottomBarItem(Route.PurchaseScreen.route, "Purchase", Icons.Default.Task),
    BottomBarItem(Route.StatisticScreen.route, "Statistic", Icons.Default.BarChart),
    BottomBarItem(Route.SettingsScreen.route, "Settings", Icons.Default.Settings),
)

private data class BottomBarItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)