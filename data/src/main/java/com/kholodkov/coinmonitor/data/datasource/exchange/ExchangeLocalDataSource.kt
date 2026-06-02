package com.kholodkov.coinmonitor.data.datasource.exchange

import com.kholodkov.coinmonitor.data.local.db.dao.ExchangeRateDao
import com.kholodkov.coinmonitor.data.local.db.mapper.toDomain
import com.kholodkov.coinmonitor.data.local.db.mapper.toDomainList
import com.kholodkov.coinmonitor.data.local.db.mapper.toEntity
import com.kholodkov.coinmonitor.data.model.exchange.NewExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExchangeLocalDataSource @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao
) {
    fun observeHasMissingRates() = exchangeRateDao.observeHasMissingRates()

    fun observeExchangeRates(): Flow<ExchangeRates> =
        exchangeRateDao.observeAll().map {
            ExchangeRates(it.toDomainList())
        }

    suspend fun getDatesWithMissingRates() = exchangeRateDao.getDatesWithMissingRates()

    suspend fun getById(id: Long) = exchangeRateDao.getById(id).toDomain()

    suspend fun insert(exchangeRate: NewExchangeRate): Long? {
        val id = exchangeRateDao.insertIfNotExists(exchangeRate.toEntity())
        return if (id == -1L) null else id
    }
}