package com.kholodkov.coinmonitor.feature.purchase.state

import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseItem
import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseState

data class PurchaseUiState(
    val plannedAmount: String = "",
    val purchases: List<PurchaseItem> = listOf(),
    val purchaseState: PurchaseState? = null
)
