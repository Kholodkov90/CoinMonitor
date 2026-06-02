package com.kholodkov.coinmonitor.feature.purchase.state

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import java.time.LocalDate

sealed interface PurchaseUiIntent {
    sealed interface Item : PurchaseUiIntent {
        data object AddNew : Item
        data class Edit(val uid: String) : Item
        data class Delete(val uid: String) : Item
        data class Restore(val params: RestorePurchaseParams) : Item
    }

    sealed interface Sheet : PurchaseUiIntent {
        data class AmountChanged(val amount: String) : Sheet
        data class DescriptionChanged(val description: String) : Sheet
        data class CurrencyChanged(val currency: Currency) : Sheet
        data object OpenDatePicker : Sheet
        data object DismissDatePicker : Sheet
        data class SetDate(val date: LocalDate) : Sheet
        data class Save(val uid: String?) : Sheet
        data class Buy(val uid: String) : Sheet
        data object Dismiss : Sheet
    }
}