package com.kholodkov.coinmonitor.feature.main.state

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import java.time.LocalDate
import java.time.LocalTime

sealed interface MainUiIntent {

    sealed interface DayNavigation : MainUiIntent {
        data object PreviousDay : DayNavigation
        data object NextDay : DayNavigation
        data object OpenDatePicker : DayNavigation
        data object DismissDatePicker : DayNavigation
        data class SelectDate(val date: LocalDate) : DayNavigation
    }

    sealed interface Item : MainUiIntent {
        data class Edit(val uid: String) : Item
        data class Delete(val uid: String) : Item
        data class Restore(val params: RestoreTransactionParams) : Item
        data object AddNew : Item
    }

    sealed interface Sheet : MainUiIntent {
        data class AmountChanged(val amount: String) : Sheet
        data class CurrencyChanged(val currency: Currency) : Sheet
        data object OpenTimeSelector : Sheet
        data object DismissTimeSelector : Sheet
        data class SetTime(val time: LocalTime) : Sheet
        data class Save(val uid: String?) : Sheet
        data object Dismiss : Sheet
    }
}
