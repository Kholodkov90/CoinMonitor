package com.kholodkov.coinmonitor.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kholodkov.coinmonitor.feature.login.screen.LoginScreenRoute

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: () -> Unit
) {
    composable(LoginRoute.ROUTE) {
        LoginScreenRoute(onLoginSuccess = onLoginSuccess)
    }
}

object LoginRoute {
    const val ROUTE = "login_screen"
}