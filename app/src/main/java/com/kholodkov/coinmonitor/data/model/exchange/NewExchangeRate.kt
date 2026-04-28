package com.kholodkov.coinmonitor.data.model.exchange

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

data class NewExchangeRate (
    val dayId: Long,
    val currency: Currency = Currency.RSD,
    val rate: BigDecimal
)