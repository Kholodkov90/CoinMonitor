package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.data.local.tools.BudgetDefaults
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import java.math.BigDecimal
import java.time.LocalDate

fun calculateBudget(
    date: LocalDate,
    exchangeRates: List<ExchangeRate>,
    currency: Currency,
    totalSpent: BigDecimal
): BigDecimal {
    return generateSequence(BudgetDefaults.START_DATE) { it.plusDays(1) }
        .takeWhile { it <= date }
        .fold(BigDecimal.ZERO) { totalBalance, day ->
            val rate = exchangeRates.rateFor(day)
            val dailyBalance = BudgetDefaults.DAILY_LIMIT.convertTo(
                from = BudgetDefaults.DEFAULT_CURRENCY,
                to = currency,
                rate = rate
            )
            totalBalance + dailyBalance
        }.minus(totalSpent)
}