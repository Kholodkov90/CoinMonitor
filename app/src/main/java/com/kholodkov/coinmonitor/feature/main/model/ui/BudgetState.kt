package com.kholodkov.coinmonitor.feature.main.model.ui

data class BudgetState(
    val balance: String = "",
    val isBalancePositive: Boolean = true,
    val spent: String = "",
    val remaining: String = "",
    val isRemainingPositive: Boolean = true
)