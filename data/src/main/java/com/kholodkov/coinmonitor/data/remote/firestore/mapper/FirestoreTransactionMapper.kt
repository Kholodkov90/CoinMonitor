package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.remote.firestore.tools.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreTransaction
import com.kholodkov.coinmonitor.data.remote.firestore.tools.parseFirestoreDate
import com.kholodkov.coinmonitor.data.remote.firestore.tools.parseFirestoreTime
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal

fun FirestoreTransaction.toRemote(uid: String, date: String) = RemoteTransaction(
    uid = uid,
    date = date.parseFirestoreDate(),
    userUid = userUid,
    amount = BigDecimal(amount),
    currency = Currency.valueOf(currency),
    time = time.parseFirestoreTime(),
    updatedAt = updatedAt
)

fun RemoteTransaction.toFirestore() = FirestoreTransaction(
    userUid = userUid,
    amount = amount.toPlainString(),
    currency = currency.name,
    time = time.formatForFirestore(),
    updatedAt = updatedAt
)