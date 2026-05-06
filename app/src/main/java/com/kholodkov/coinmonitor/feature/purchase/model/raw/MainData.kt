package com.kholodkov.coinmonitor.feature.purchase.model.raw

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import java.math.BigDecimal

data class MainData(
    val plannedAmount: BigDecimal = BigDecimal.ZERO,
    val plannedCurrency: Currency = Currency.RSD,
    val purchases: List<PurchaseProjection> = listOf()
)