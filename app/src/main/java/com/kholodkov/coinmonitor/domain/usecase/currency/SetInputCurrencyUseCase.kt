package com.kholodkov.coinmonitor.domain.usecase.currency

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetInputCurrencyUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(currency: Currency) {
        preferencesRepository.setInputCurrency(currency)
    }
}