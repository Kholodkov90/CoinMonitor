package com.kholodkov.coinmonitor.data.mapper

import com.kholodkov.coinmonitor.data.model.purchase.BoughtPurchase
import com.kholodkov.coinmonitor.data.model.purchase.EditedPurchase
import com.kholodkov.coinmonitor.data.model.purchase.NewPurchase
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.ResolvedPurchase
import com.kholodkov.coinmonitor.data.model.transaction.NewTransaction
import com.kholodkov.coinmonitor.domain.model.purchase.EditPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.NewPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import java.time.Instant
import java.time.LocalTime

fun RemotePurchase.toResolved(dayId: Long, userId: Long, transactionId: Long?) = ResolvedPurchase(
    uid = uid,
    dayId = dayId,
    userId = userId,
    transactionId = transactionId,
    amount = amount,
    currency = currency,
    description = description,
    updatedAt = updatedAt
)

fun NewPurchaseParams.toNewPurchase(uid: String, dayId: Long, userId: Long) = NewPurchase(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    transactionId = null,
    description = description,
    updatedAt = Instant.now()
)

fun RestorePurchaseParams.toNewPurchase(dayId: Long, userId: Long, transactionId: Long?) =
    NewPurchase(
        uid = uid,
        dayId = dayId,
        userId = userId,
        amount = amount,
        currency = currency,
        transactionId = transactionId,
        description = description,
        updatedAt = updatedAt
    )

fun EditPurchaseParams.toEditedPurchase(dayId: Long) = EditedPurchase(
    uid = uid,
    amount = amount,
    currency = currency,
    dayId = dayId,
    description = description
)

fun EditPurchaseParams.toNewTransaction(uid: String, dayId: Long, userId: Long) = NewTransaction(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    time = LocalTime.now(),
    updatedAt = Instant.now()
)

fun EditPurchaseParams.toBoughtPurchase(dayId: Long, transactionId: Long) = BoughtPurchase(
    uid = uid,
    amount = amount,
    currency = currency,
    dayId = dayId,
    transactionId = transactionId,
    description = description
)