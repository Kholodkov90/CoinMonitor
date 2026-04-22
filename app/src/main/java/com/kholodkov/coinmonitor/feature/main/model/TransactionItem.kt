package com.kholodkov.coinmonitor.feature.main.model

data class TransactionItem(
    val uid: String,
    val amount: String,
    val time: String,
    val user: String
)