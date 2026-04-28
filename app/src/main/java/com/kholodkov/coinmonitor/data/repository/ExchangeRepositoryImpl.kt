package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.day.DayDataSource
import com.kholodkov.coinmonitor.data.datasource.exchange.ExchangeDataSource
import com.kholodkov.coinmonitor.data.model.exchange.NewExchangeRate
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeDataSource: ExchangeDataSource,
    private val dayDataSource: DayDataSource
) : ExchangeRepository {
    override suspend fun updateExchangeRates() = runCatching {
        exchangeDataSource.getDatesWithMissingRates().forEach { date ->
            val rate = exchangeDataSource.fetchRate(date)
                ?: error("Can't load rate for $date")
            val dayId = dayDataSource.getOrCreateDayId(date)
            exchangeDataSource.addNew(
                NewExchangeRate(
                    dayId = dayId,
                    rate = rate
                )
            )
        }
    }

    override fun observeAll() = exchangeDataSource.observeAll()
}