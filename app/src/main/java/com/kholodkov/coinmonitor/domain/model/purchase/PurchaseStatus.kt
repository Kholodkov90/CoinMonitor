package com.kholodkov.coinmonitor.domain.model.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

sealed class PurchaseStatus {
    data object Overdue : PurchaseStatus()
    data object Completed : PurchaseStatus()
    data object Available : PurchaseStatus()
    data class Unreachable(val gap: BigDecimal, val currency: Currency) : PurchaseStatus()
    data class Pending(val dailyLimit: BigDecimal, val currency: Currency) : PurchaseStatus()
}