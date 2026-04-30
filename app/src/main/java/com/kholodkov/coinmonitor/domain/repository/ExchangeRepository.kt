package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    suspend fun updateExchangeRates(): Result<Unit>
    fun observeExchangeRates(): Flow<ExchangeRates>
}