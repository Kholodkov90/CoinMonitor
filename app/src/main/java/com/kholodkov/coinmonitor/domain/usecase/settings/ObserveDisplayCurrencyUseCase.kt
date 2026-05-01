package com.kholodkov.coinmonitor.domain.usecase.settings

import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import javax.inject.Inject

class ObserveDisplayCurrencyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.observeDisplayCurrency()
}