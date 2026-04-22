package com.kholodkov.coinmonitor.domain.model

import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

data class CurrencySum(
    val currency: Currency,
    val amount: BigDecimal
)