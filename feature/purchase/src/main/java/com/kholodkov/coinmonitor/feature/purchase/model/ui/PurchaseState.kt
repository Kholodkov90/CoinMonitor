package com.kholodkov.coinmonitor.feature.purchase.model.ui

import com.kholodkov.coinmonitor.domain.model.currency.Currency

data class PurchaseState(
    val uid: String?,
    val description: String,
    val amount: String,
    val date: String,
    val currency: Currency,
    val isDateSelectorVisible: Boolean,
    val isBuyButtonVisible: Boolean,
    val isButtonsEnabled: Boolean
)