package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

data class PurchaseSummary(
    val totalAmount: BigDecimal,
    val currency: Currency
)