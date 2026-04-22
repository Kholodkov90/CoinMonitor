package com.kholodkov.coinmonitor.feature.main.state

import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.domain.model.Currency
import com.kholodkov.coinmonitor.feature.main.model.TransactionItem
import java.math.BigDecimal

data class MainUiState(
    val date: String = "",
    val dateDescription: String = "",
    val balance: String = "",
    val spent: String = "",
    val remaining: String = "",
    val inputAmount: String = "",
    val inputCurrency: Currency = Currency.RSD,
    val inputTime: String = "",
    val isTimeSelectorOpened: Boolean = false,
    val editTransactionUid: String? = null,
    val transactions: List<TransactionItem> = listOf(),
    val isTransactionSheetVisible: Boolean = false,
    //TODO: Use variable "isDatePickerVisible" instead of "showDatePicker by remember"
    val isDatePickerVisible: Boolean = false,
) {

    val isSaveAvailable: Boolean
        get() = inputAmount.parseToBigDecimal()?.let { it > BigDecimal.ZERO } == true
}

