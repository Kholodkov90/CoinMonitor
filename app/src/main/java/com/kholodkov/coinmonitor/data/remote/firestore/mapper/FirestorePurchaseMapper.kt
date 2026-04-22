package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.remote.firestore.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestorePurchase
import com.kholodkov.coinmonitor.data.remote.firestore.parseFirestoreDate
import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

fun FirestorePurchase.toRemote() = RemotePurchase(
    uid = uid,
    date = date.parseFirestoreDate(),
    userUid = userUid,
    amount = BigDecimal(amount),
    currency = Currency.valueOf(currency),
    description = description,
    updatedAt = updatedAt
)

fun RemotePurchase.toFirestore() = FirestorePurchase(
    uid = uid,
    date = date.formatForFirestore(),
    userUid = userUid,
    amount = amount.toPlainString(),
    currency = currency.name,
    description = description,
    updatedAt = updatedAt
)