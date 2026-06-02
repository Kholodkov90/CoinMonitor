package com.kholodkov.coinmonitor.data.remote.exchange

import com.kholodkov.coinmonitor.data.remote.exchange.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeApi {
    @GET("v2/rate/EUR/RSD")
    suspend fun getRate(@Query("from") date: String): ExchangeRateResponse
}
