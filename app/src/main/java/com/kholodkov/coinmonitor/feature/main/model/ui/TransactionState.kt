package com.kholodkov.coinmonitor.feature.main.model.ui

import com.kholodkov.coinmonitor.domain.model.currency.Currency

data class TransactionState(
    val uid: String?,
    val amount: String,
    val currency: Currency,
    val time: String,
    val isSaveEnabled: Boolean,
    val isTimePickerVisible: Boolean
)