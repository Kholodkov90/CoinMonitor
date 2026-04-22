package com.kholodkov.coinmonitor.data.remote.exchange.model

import java.math.BigDecimal

data class ExchangeRateResponse(
    val date: String,
    val base: String,
    val quote: String,
    val rate: BigDecimal
)