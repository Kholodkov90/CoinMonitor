package com.kholodkov.coinmonitor.feature.settings.state

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.time.DayOfWeek

data class SettingsUiState(
    val name: String = "",
    val currency: Currency = Currency.RSD,
    val startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val isEditNameDialogVisible: Boolean = false
)
