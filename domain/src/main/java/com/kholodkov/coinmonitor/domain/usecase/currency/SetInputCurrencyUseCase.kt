package com.kholodkov.coinmonitor.domain.usecase.currency

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import javax.inject.Inject

class SetInputCurrencyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(currency: Currency) {
        settingsRepository.setInputCurrency(currency)
    }
}