package com.kholodkov.coinmonitor.data.remote.exchange.model

import androidx.annotation.Keep
import java.math.BigDecimal

@Keep
data class ExchangeRateResponse(
    val date: String,
    val base: String,
    val quote: String,
    val rate: BigDecimal
)