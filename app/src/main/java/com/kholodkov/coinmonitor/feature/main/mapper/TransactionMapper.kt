package com.kholodkov.coinmonitor.feature.main.mapper

import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import com.kholodkov.coinmonitor.feature.main.model.TransactionItem


fun Transaction.toItem() = TransactionItem(
    uid = uid,
    amount = "${amount.toDisplayString()} ${currency.name}",
    time = time.toDisplayString(),
    user = user
)

fun Transaction.toRestoreTransactionParams() = RestoreTransactionParams(
    uid = uid,
    userUid = userUid,
    date = date,
    amount = amount,
    currency = currency,
    time = time,
    updatedAt = updatedAt
)

fun List<Transaction>.toItemList() = map { it.toItem() }