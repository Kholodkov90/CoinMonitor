package com.kholodkov.coinmonitor.domain.repository

interface ExchangeRepository {
    suspend fun updateExchangeRates(): Result<Unit>
}