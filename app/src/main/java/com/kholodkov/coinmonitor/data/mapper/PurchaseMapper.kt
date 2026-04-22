package com.kholodkov.coinmonitor.data.mapper

import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.ResolvedPurchase

fun RemotePurchase.toResolved(dayId: Long, userId: Long) = ResolvedPurchase(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    description = description,
    updatedAt = updatedAt
)