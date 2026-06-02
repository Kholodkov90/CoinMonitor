package com.kholodkov.coinmonitor.feature.purchase.model.raw

import java.time.LocalDate

data class PurchaseData(
    val uid: String?,
    val amount: String,
    val description: String,
    val date: LocalDate,
    val transactionUid: String? = null,
    val isDatePickerVisible: Boolean = false
)