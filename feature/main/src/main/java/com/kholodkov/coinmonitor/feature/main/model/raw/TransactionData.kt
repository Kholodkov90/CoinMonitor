package com.kholodkov.coinmonitor.feature.main.model.raw

data class TransactionData(
    val uid: String?,
    val amount: String,
    val time: String,
    val isTimePickerVisible: Boolean
)