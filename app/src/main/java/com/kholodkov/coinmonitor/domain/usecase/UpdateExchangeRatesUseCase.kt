package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import javax.inject.Inject

class UpdateExchangeRatesUseCase @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) {
    suspend operator fun invoke() = exchangeRepository.updateExchangeRates()
}