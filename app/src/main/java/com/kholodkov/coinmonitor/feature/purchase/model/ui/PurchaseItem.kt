package com.kholodkov.coinmonitor.feature.purchase.model.ui

import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus

data class PurchaseItem(
    val uid: String,
    val description: String,
    val amount: String,
    val date: String,
    val status: PurchaseStatus,
    val user: String
)