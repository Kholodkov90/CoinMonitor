package com.kholodkov.coinmonitor.feature.settings.ui.state

sealed interface SettingsUiIntent {
    data object SignOut : SettingsUiIntent
}