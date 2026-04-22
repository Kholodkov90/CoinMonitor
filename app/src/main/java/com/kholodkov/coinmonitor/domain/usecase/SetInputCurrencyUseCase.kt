package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.domain.model.Currency
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetInputCurrencyUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(currency: Currency) {
        preferencesRepository.setInputCurrency(currency)
    }
}