package com.kholodkov.coinmonitor.core.navigation

sealed class Route(val route: String) {
    object AuthGraph : Route("auth_graph")
    object HomeGraph : Route("home_graph")

    object LoginScreen : Route("login_screen")
    object MainScreen : Route("main_screen")
    object PurchaseScreen : Route("purchase_screen")
    object StatisticScreen : Route("statistic_screen")
    object SettingsScreen : Route("settings_screen")
}