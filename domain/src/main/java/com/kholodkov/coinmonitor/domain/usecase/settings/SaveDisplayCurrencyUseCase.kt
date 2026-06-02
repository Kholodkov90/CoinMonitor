package com.kholodkov.coinmonitor.domain.usecase.settings

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveDisplayCurrencyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(currency: Currency) {
        settingsRepository.setDisplayCurrency(currency)
    }
}