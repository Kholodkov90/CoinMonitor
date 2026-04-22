package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.remote.firestore.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreTransaction
import com.kholodkov.coinmonitor.data.remote.firestore.parseFirestoreDate
import com.kholodkov.coinmonitor.data.remote.firestore.parseFirestoreTime
import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal

fun FirestoreTransaction.toRemote() = RemoteTransaction(
    uid = uid,
    date = date.parseFirestoreDate(),
    userUid = userUid,
    amount = BigDecimal(amount),
    currency = Currency.valueOf(currency),
    time = time.parseFirestoreTime(),
    updatedAt = updatedAt
)

fun RemoteTransaction.toFirestore() = FirestoreTransaction(
    uid = uid,
    date = date.formatForFirestore(),
    userUid = userUid,
    amount = amount.toPlainString(),
    currency = currency.name,
    time = time.formatForFirestore(),
    updatedAt = updatedAt
)