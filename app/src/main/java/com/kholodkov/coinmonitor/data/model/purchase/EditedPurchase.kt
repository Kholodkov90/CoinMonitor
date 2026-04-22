package com.kholodkov.coinmonitor.data.model.purchase

import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

data class EditedPurchase(
    val uid: String,
    val amount: BigDecimal,
    val currency: Currency,
    val dayId: Long,
    val description: String
)