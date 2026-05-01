package com.kholodkov.coinmonitor.feature.settings.state

sealed class SettingsUiEvent {
    data object Exit : SettingsUiEvent()
}