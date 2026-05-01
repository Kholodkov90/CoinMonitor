package com.kholodkov.coinmonitor.feature.settings.state

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.time.DayOfWeek

sealed interface SettingsUiIntent {
    data object EditName : SettingsUiIntent
    data object HideEditNameDialog : SettingsUiIntent
    data class SaveName(val name: String) : SettingsUiIntent
    data class SelectCurrency(val currency: Currency) : SettingsUiIntent
    data class SelectStartOfWeek(val day: DayOfWeek) : SettingsUiIntent
    data object SignOut : SettingsUiIntent
}