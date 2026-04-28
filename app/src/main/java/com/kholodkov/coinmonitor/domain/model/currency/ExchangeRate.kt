package com.kholodkov.coinmonitor.domain.model.currency

import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeRate(
    val date: LocalDate,
    val currency: Currency = Currency.RSD,
    val exchangeRate: BigDecimal
)