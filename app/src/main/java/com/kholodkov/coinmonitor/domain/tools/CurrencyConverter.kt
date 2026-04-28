package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.convertTo(
    from: Currency,
    to: Currency,
    rate: BigDecimal
): BigDecimal {
    if (from == to) return this
    if (to == Currency.RSD) return this.multiply(rate)
    return this.divide(rate, 2, RoundingMode.HALF_UP)
}