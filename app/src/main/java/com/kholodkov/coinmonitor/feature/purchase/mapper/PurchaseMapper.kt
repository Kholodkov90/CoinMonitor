package com.kholodkov.coinmonitor.feature.purchase.mapper

import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseItem
import com.kholodkov.coinmonitor.feature.purchase.model.raw.PurchaseData
import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseState
import java.math.BigDecimal

fun PurchaseProjection.toPurchaseItem() = PurchaseItem(
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

fun PurchaseData.toPurchaseState(currency: Currency) = PurchaseState(
    uid = uid,
    description = description,
    amount = amount,
    date = date.toDisplayString(),
    currency = currency,
    isDateSelectorVisible = isDatePickerVisible,
    isBuyButtonVisible = uid != null && transactionUid == null,
    isButtonsEnabled = amount.parseToBigDecimal()?.let { it > BigDecimal.ZERO } == true
            && description.isNotBlank()
)

fun List<PurchaseProjection>.toPurchaseItemList() = map { it.toPurchaseItem() }