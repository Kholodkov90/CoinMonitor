package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.day.DayDataSource
import com.kholodkov.coinmonitor.data.datasource.exchange.ExchangeDataSource
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeDataSource: ExchangeDataSource,
    private val dayDataSource: DayDataSource
) : ExchangeRepository {
    override suspend fun updateExchangeRates() = runCatching {
        dayDataSource.getDaysWithoutRate().forEach { day ->
            val rate = exchangeDataSource.getEurToRsd(day.date)
                ?: error("Can't load rate for ${day.date}")
            dayDataSource.updateRate(day.copy(exchangeRate = rate))
        }
    }
}