package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate.ExchangeRateEntity
import com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate.FullExchangeRateEntity
import com.kholodkov.coinmonitor.data.model.exchange.NewExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate

fun NewExchangeRate.toEntity() = ExchangeRateEntity(
    dayId = dayId,
    currency = currency,
    exchangeRate = rate
)

fun FullExchangeRateEntity.toDomain() = ExchangeRate(
    date = date,
    currency = currency,
    exchangeRate = exchangeRate
)

fun List<FullExchangeRateEntity>.toDomainList() = map { it.toDomain() }