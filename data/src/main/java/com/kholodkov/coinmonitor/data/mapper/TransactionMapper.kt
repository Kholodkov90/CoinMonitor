package com.kholodkov.coinmonitor.data.mapper

import com.kholodkov.coinmonitor.data.model.transaction.EditedTransaction
import com.kholodkov.coinmonitor.data.model.transaction.NewTransaction
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.model.transaction.ResolvedTransaction
import com.kholodkov.coinmonitor.domain.model.transaction.EditTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.NewTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import java.time.Instant

fun RemoteTransaction.toResolved(dayId: Long, userId: Long) = ResolvedTransaction(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = updatedAt
)

fun NewTransactionParams.toNewTransaction(uid: String, dayId: Long, userId: Long) = NewTransaction(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = Instant.now()
)

fun RestoreTransactionParams.toNewTransaction(dayId: Long, userId: Long) = NewTransaction(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = updatedAt
)

fun EditTransactionParams.toEditedTransaction() = EditedTransaction(
    uid = uid,
    amount = amount,
    currency = currency,
    time = time
)