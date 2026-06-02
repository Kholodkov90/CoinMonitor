package com.kholodkov.coinmonitor.data.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

data class BoughtPurchase(
    val uid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val transactionId: Long,
    val dayId: Long,
    val description: String
)