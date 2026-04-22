package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.purchase.FullPurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseSyncEntity
import com.kholodkov.coinmonitor.data.model.purchase.NewPurchase
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.ResolvedPurchase
import com.kholodkov.coinmonitor.domain.model.Purchase
import java.time.Instant

fun ResolvedPurchase.toEntity() = PurchaseEntity(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    description = description,
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun PurchaseSyncEntity.toRemote() = RemotePurchase(
    uid = uid,
    date = date,
    userUid = userUid,
    amount = amount,
    currency = currency,
    description = description,
    updatedAt = updatedAt.toEpochMilli()
)

fun NewPurchase.toEntity() = PurchaseEntity(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    description = description,
    updatedAt = Instant.now()
)

fun FullPurchaseEntity.toDomain() = Purchase(
    uid = uid,
    date = date,
    amount = amount,
    currency = currency,
    description = description,
    userName = userName
)

fun List<FullPurchaseEntity>.toDomainList() = map { it.toDomain() }