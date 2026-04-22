package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.transaction.FullTransactionEntity
import com.kholodkov.coinmonitor.data.local.db.entity.transaction.TransactionEntity
import com.kholodkov.coinmonitor.data.local.db.entity.transaction.TransactionSyncEntity
import com.kholodkov.coinmonitor.data.model.transaction.NewTransaction
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.model.transaction.ResolvedTransaction
import com.kholodkov.coinmonitor.domain.model.Transaction
import java.time.Instant

fun ResolvedTransaction.toEntity() = TransactionEntity(
    uid = uid,
    dayId = dayId,
    userId = userId,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = Instant.ofEpochMilli(updatedAt)
)


fun FullTransactionEntity.toDomain() = Transaction(
    uid = uid,
    userUid = userUid,
    date = date,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = updatedAt,
    user = userName
)

fun TransactionSyncEntity.toRemote() = RemoteTransaction(
    uid = uid,
    date = date,
    amount = amount,
    currency = currency,
    time = time,
    userUid = userUid,
    updatedAt = updatedAt.toEpochMilli()
)

fun NewTransaction.toEntity() = TransactionEntity(
    uid = uid,
    userId = userId,
    dayId = dayId,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = Instant.now()
)

fun List<FullTransactionEntity>.toDomainList() = map { it.toDomain() }