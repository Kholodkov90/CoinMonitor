package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.data.local.tools.BudgetDefaults
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import java.time.LocalDate

fun List<ExchangeRate>.rateFor(date: LocalDate) = filter { it.date <= date }
    .maxByOrNull { it.date }
    ?.exchangeRate
    ?: BudgetDefaults.DEFAULT_RATE