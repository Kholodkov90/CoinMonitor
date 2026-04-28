package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestorePurchase
import com.kholodkov.coinmonitor.data.remote.firestore.tools.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.tools.parseFirestoreDate
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

fun FirestorePurchase.toRemote(uid: String) = RemotePurchase(
    uid = uid,
    date = date.parseFirestoreDate(),
    userUid = userUid,
    amount = BigDecimal(amount),
    transactionUid = transactionUid,
    currency = Currency.valueOf(currency),
    description = description,
    updatedAt = updatedAt
)

fun RemotePurchase.toFirestore() = FirestorePurchase(
    userUid = userUid,
    date = date.formatForFirestore(),
    amount = amount.toPlainString(),
    transactionUid = transactionUid,
    currency = currency.name,
    description = description,
    updatedAt = updatedAt
)