package com.kholodkov.coinmonitor.data.datasource.exchange

import com.kholodkov.coinmonitor.data.remote.exchange.ExchangeApi
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ExchangeDataSource @Inject constructor(
    private val api: ExchangeApi
) {
    suspend fun getEurToRsd(date: LocalDate): BigDecimal? = runCatching {
        api.getRate(date.toString()).rate
    }.getOrNull()
}