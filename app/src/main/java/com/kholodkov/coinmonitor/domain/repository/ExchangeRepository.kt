package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    suspend fun updateExchangeRates(): Result<Unit>
    fun observeAll(): Flow<List<ExchangeRate>>
}