package com.kholodkov.coinmonitor.feature.main.state

import com.kholodkov.coinmonitor.feature.main.model.ui.BudgetState
import com.kholodkov.coinmonitor.feature.main.model.ui.DayState
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionItem
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionState

data class MainUiState(
    val dayState: DayState = DayState(),
    val budgetState: BudgetState = BudgetState(),
    val transactions: List<TransactionItem> = listOf(),
    val transactionState: TransactionState? = null
)

