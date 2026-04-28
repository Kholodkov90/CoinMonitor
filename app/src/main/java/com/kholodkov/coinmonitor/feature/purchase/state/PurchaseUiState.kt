package com.kholodkov.coinmonitor.feature.purchase.state

import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.feature.purchase.model.PurchaseItem
import java.math.BigDecimal

data class PurchaseUiState(
    val planedAmount: String = "",
    val purchases: List<PurchaseItem> = listOf(),
    val isPurchaseSheetVisible: Boolean = false,
    val inputUid: String? = "",
    val inputDescription: String = "",
    val inputAmount: String = "",
    val inputDate: String = "",
    val inputCurrency: Currency = Currency.RSD,
    val isDateSelectorOpened: Boolean = false,
    val isBuyButtonVisible: Boolean = true,
) {
    val isButtonsEnabled: Boolean
        get() = inputAmount.parseToBigDecimal()?.let { it > BigDecimal.ZERO } == true
                && inputDescription.isNotBlank()
}

