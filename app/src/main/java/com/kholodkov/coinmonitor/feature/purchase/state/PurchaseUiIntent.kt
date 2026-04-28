package com.kholodkov.coinmonitor.feature.purchase.state

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import java.time.LocalDate

sealed interface PurchaseUiIntent {
    // Main intents
    data object AddPurchase : PurchaseUiIntent
    data class EditPurchase(val uid: String) : PurchaseUiIntent
    data class DeletePurchase(val uid: String) : PurchaseUiIntent
    data class RestorePurchase(val params: RestorePurchaseParams) : PurchaseUiIntent

    // Purchase sheet intents
    data object HidePurchaseSheet : PurchaseUiIntent
    data class EditAmount(val amount: String) : PurchaseUiIntent
    data class EditDescription(val description: String) : PurchaseUiIntent
    data class EditCurrency(val currency: Currency) : PurchaseUiIntent
    data object ShowDatePicker : PurchaseUiIntent
    data class SetDate(val date: LocalDate) : PurchaseUiIntent
    data object DismissDateSelector : PurchaseUiIntent
    data class SavePurchase(val uid: String?) : PurchaseUiIntent
    data class BuyPurchase(val uid: String) : PurchaseUiIntent

}