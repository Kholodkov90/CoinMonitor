package com.kholodkov.coinmonitor.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kholodkov.coinmonitor.R

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
                label = { Text(stringResource(item.title)) }
            )
        }
    }
}

private val bottomBarItems = listOf(
    BottomBarItem(Route.MainScreen.route, R.string.tab_expenses, Icons.Default.Wallet),
    BottomBarItem(Route.PurchaseScreen.route, R.string.tab_purchases, Icons.Default.Receipt),
    BottomBarItem(Route.StatisticScreen.route, R.string.tab_statistic, Icons.Default.BarChart),
    BottomBarItem(Route.SettingsScreen.route, R.string.tab_settings, Icons.Default.Settings),
)

private data class BottomBarItem(
    val route: String,
    @field:StringRes val title: Int,
    val icon: ImageVector
)