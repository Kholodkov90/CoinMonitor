package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.data.local.tools.BudgetDefaults
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import java.math.BigDecimal
import java.time.LocalDate

fun calculateBudget(
    date: LocalDate,
    exchangeRates: ExchangeRates,
    currency: Currency,
    totalSpent: BigDecimal
): BigDecimal {
    return generateSequence(BudgetDefaults.START_DATE) { it.plusDays(1) }
        .takeWhile { it <= date }
        .fold(BigDecimal.ZERO) { totalAmount, date ->
            val dailyBalance = exchangeRates.convert(
                amount = BudgetDefaults.DAILY_LIMIT,
                from = BudgetDefaults.DEFAULT_CURRENCY,
                to = currency,
                date = date
            )
            totalAmount + dailyBalance
        }.minus(totalSpent)
}