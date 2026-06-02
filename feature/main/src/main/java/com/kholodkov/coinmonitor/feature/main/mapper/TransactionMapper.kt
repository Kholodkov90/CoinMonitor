package com.kholodkov.coinmonitor.feature.main.mapper

import com.kholodkov.coinmonitor.core.common.parseToBigDecimal
import com.kholodkov.coinmonitor.core.common.toDisplayString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import com.kholodkov.coinmonitor.feature.main.model.raw.TransactionData
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionItem
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionState
import java.math.BigDecimal

fun Transaction.toTransactionItem() = TransactionItem(
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

fun TransactionData.toTransactionState(currency: Currency) = TransactionState(
    uid = uid,
    amount = amount,
    currency = currency,
    time = time,
    isSaveEnabled = amount.parseToBigDecimal()?.let { it > BigDecimal.ZERO } ?: false,
    isTimePickerVisible = isTimePickerVisible
)

fun List<Transaction>.toTransactionItemList() = map { it.toTransactionItem() }