package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.summary.CurrencySumEntity
import com.kholodkov.coinmonitor.domain.model.CurrencySum

fun CurrencySumEntity.toDomain() = CurrencySum(
    currency = currency,
    amount = amount
)

fun List<CurrencySumEntity>.toDomainList() = map { it.toDomain() }