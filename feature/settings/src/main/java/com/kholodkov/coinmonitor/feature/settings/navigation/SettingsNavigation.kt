package com.kholodkov.coinmonitor.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kholodkov.coinmonitor.feature.settings.screen.SettingsScreenRoute

fun NavGraphBuilder.settingsScreen(
    onSignOut: () -> Unit
) {
    composable(SettingsRoute.ROUTE) {
        SettingsScreenRoute(onSignOut = onSignOut)
    }
}

object SettingsRoute {
    const val ROUTE = "settings_screen"
}