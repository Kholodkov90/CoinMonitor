package com.kholodkov.coinmonitor.feature.main.state

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import java.time.LocalDate
import java.time.LocalTime

sealed interface MainUiIntent {
    // Main intents
    data class SelectDate(val date: LocalDate) : MainUiIntent
    data object NextDay : MainUiIntent
    data object PreviousDay : MainUiIntent
    data class EditTransaction(val uid: String) : MainUiIntent
    data class DeleteTransaction(val uid: String) : MainUiIntent
    data class RestoreTransaction(val params: RestoreTransactionParams) : MainUiIntent
    data object AddNewTransaction : MainUiIntent

    //Transaction sheet intents
    data object HideTransactionSheet : MainUiIntent
    data object EditTime : MainUiIntent
    data class SaveTransaction(val uid: String?) : MainUiIntent
    data class EditAmount(val amount: String) : MainUiIntent
    data class EditCurrency(val currency: Currency) : MainUiIntent

    //Time selector intents
    data class SetTime(val time: LocalTime) : MainUiIntent
    data object DismissTimeSelector : MainUiIntent
}
