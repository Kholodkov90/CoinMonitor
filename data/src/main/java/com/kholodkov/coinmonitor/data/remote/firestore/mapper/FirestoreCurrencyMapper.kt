package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreCurrency
import com.kholodkov.coinmonitor.data.remote.firestore.tools.parseFirestoreDate
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate

fun FirestoreCurrency.toExchangeRate(date: String, currency: Currency) = ExchangeRate(
    date = date.parseFirestoreDate(),
    currency = currency,
    exchangeRate = exchangeRate.toBigDecimal()
)

fun ExchangeRate.toFirestore() = FirestoreCurrency(
    exchangeRate = exchangeRate.toDouble()
)