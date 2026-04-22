package com.kholodkov.coinmonitor.data.model.purchase

import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

data class NewPurchase(
    val uid: String,
    val dayId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val currency: Currency,
    val description: String
)