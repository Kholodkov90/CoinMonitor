package com.kholodkov.coinmonitor.feature.purchase.state

import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams

sealed interface PurchaseUiEvent {
    data class ShowRestorePurchaseSnackbar(val params: RestorePurchaseParams) : PurchaseUiEvent
}