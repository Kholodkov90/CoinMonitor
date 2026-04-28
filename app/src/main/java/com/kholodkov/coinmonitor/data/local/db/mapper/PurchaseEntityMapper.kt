package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.purchase.FullPurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PlannedPurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseSyncEntity
import com.kholodkov.coinmonitor.data.model.purchase.NewPurchase
import com.kholodkov.coinmonitor.data.model.purchase.PlannedPurchase
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.ResolvedPurchase
import com.kholodkov.coinmonitor.domain.model.purchase.Purchase
import java.time.Instant

fun ResolvedPurchase.toEntity() = PurchaseEntity(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    transactionId = transactionId,
    currency = currency,
    description = description,
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun PurchaseSyncEntity.toRemote() = RemotePurchase(
    uid = uid,
    date = date,
    userUid = userUid,
    amount = amount,
    transactionUid = transactionUid,
    currency = currency,
    description = description,
    updatedAt = updatedAt.toEpochMilli()
)

fun NewPurchase.toEntity() = PurchaseEntity(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    transactionId = transactionId,
    currency = currency,
    description = description,
    updatedAt = updatedAt
)

fun FullPurchaseEntity.toDomain() = Purchase(
    uid = uid,
    date = date,
    userUid = userUid,
    amount = amount,
    transactionUid = transactionUid,
    currency = currency,
    description = description,
    userName = userName,
    updatedAt = updatedAt
)

fun PlannedPurchaseEntity.toDomain() = PlannedPurchase(
    amount = amount,
    currency = currency,
    date = date
)

fun List<FullPurchaseEntity>.toDomainList() = map { it.toDomain() }

fun List<PlannedPurchaseEntity>.toPlannedPurchases() = map { it.toDomain() }
