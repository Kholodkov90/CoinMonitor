package com.kholodkov.coinmonitor.feature.settings.ui.state

sealed class SettingsUiEvent {
    data object Exit : SettingsUiEvent()
}