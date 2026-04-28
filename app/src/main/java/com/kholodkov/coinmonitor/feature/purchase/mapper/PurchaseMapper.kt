package com.kholodkov.coinmonitor.feature.purchase.mapper

import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import com.kholodkov.coinmonitor.feature.purchase.model.PurchaseItem

fun PurchaseProjection.toItem() = PurchaseItem(
    uid = uid,
    description = description,
    amount = "${amount.toDisplayString()} ${currency.name}",
    date = date.toDisplayString(),
    status = status,
    user = userName
)

fun PurchaseProjection.toRestorePurchaseParams() = RestorePurchaseParams(
    date = date,
    uid = uid,
    userUid = userUid,
    amount = amount,
    currency = currency,
    transactionUid = transactionUid,
    description = description,
    updatedAt = updatedAt
)

fun List<PurchaseProjection>.toItemList() = map { it.toItem() }