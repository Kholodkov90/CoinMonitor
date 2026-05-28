package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun calculateBudget(
    date: LocalDate,
    totalSpent: BigDecimal,
    appConfig: AppConfig,
): BigDecimal {
    val days = ChronoUnit.DAYS.between(appConfig.startDate, date) + 1
    if (days <= 0) return BigDecimal.ZERO
    return appConfig.dailyLimit.multiply(days.toBigDecimal())
        .minus(totalSpent)
        .plus(appConfig.initialBalance)
}