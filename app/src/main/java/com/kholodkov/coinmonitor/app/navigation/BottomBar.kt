package com.kholodkov.coinmonitor.app.navigation

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
import com.kholodkov.coinmonitor.feature.main.navigation.MainRoute
import com.kholodkov.coinmonitor.feature.purchase.navigation.PurchasesRoute
import com.kholodkov.coinmonitor.feature.settings.navigation.SettingsRoute
import com.kholodkov.coinmonitor.feature.statistic.navigation.StatisticRoute

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
                        popUpTo(AppGraph.HOME) { saveState = true }
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, null) },
                label = { Text(stringResource(item.title)) }
            )
        }
    }
}

private val bottomBarItems = listOf(
    BottomBarItem(MainRoute.ROUTE, R.string.tab_expenses, Icons.Default.Wallet),
    BottomBarItem(PurchasesRoute.ROUTE, R.string.tab_purchases, Icons.Default.Receipt),
    BottomBarItem(StatisticRoute.ROUTE, R.string.tab_statistic, Icons.Default.BarChart),
    BottomBarItem(SettingsRoute.ROUTE, R.string.tab_settings, Icons.Default.Settings),
)

private data class BottomBarItem(
    val route: String,
    @field:StringRes val title: Int,
    val icon: ImageVector
)