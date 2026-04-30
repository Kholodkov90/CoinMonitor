package com.kholodkov.coinmonitor.data.datasource.exchange

import com.kholodkov.coinmonitor.data.model.exchange.NewExchangeRate
import java.time.LocalDate
import javax.inject.Inject

class ExchangeDataSource @Inject constructor(
    private val remoteDataSource: ExchangeRemoteDataSource,
    private val localDataSource: ExchangeLocalDataSource
) {
    suspend fun fetchRate(date: LocalDate) = remoteDataSource.getEurToRsd(date)

    fun observeHasMissingRates() = localDataSource.observeHasMissingRates()

    fun observeRemoteChanges() = remoteDataSource.observeChanges()

    fun observeExchangeRates() = localDataSource.observeExchangeRates()

    suspend fun getDatesWithMissingRates() = localDataSource.getDatesWithMissingRates()

    suspend fun addFromRemote(exchangeRate: NewExchangeRate) {
        localDataSource.insert(exchangeRate)
    }

    suspend fun addNew(exchangeRate: NewExchangeRate) {
        localDataSource.insert(exchangeRate)?.let { id ->
            val remoteModel = localDataSource.getById(id)
            remoteDataSource.push(remoteModel)
        }
    }
}