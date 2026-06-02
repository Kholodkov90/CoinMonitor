package com.kholodkov.coinmonitor.feature.purchase.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kholodkov.coinmonitor.feature.purchase.screen.PurchaseScreenRoute

fun NavGraphBuilder.purchasesScreen() {
    composable(PurchasesRoute.ROUTE) {
        PurchaseScreenRoute()
    }
}

object PurchasesRoute {
    const val ROUTE = "purchases_screen"
}