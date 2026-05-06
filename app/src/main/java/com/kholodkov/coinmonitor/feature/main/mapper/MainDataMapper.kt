package com.kholodkov.coinmonitor.feature.main.mapper

import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.feature.main.model.raw.MainData
import com.kholodkov.coinmonitor.feature.main.model.ui.BudgetState
import com.kholodkov.coinmonitor.feature.main.model.ui.DayState
import java.math.BigDecimal
import java.time.LocalDate

fun MainData.toDayState(isDatePickerVisible : Boolean): DayState {
    val today = LocalDate.now()
    return DayState(
        date = date.toDisplayString(),
        dateDescriptionRes = when (date) {
            today -> R.string.date_today
            today.minusDays(1) -> R.string.date_yesterday
            today.plusDays(1) -> R.string.date_tomorrow
            else -> null
        },
        isDatePickerVisible = isDatePickerVisible
    )
}

fun MainData.toBudgetState() = BudgetState(
    balance = "${balance.toDisplayString()} $currency",
    isBalancePositive = balance >= BigDecimal.ZERO,
    spent = "${spent.toDisplayString()} $currency",
    remaining = "${remaining.toDisplayString()} $currency",
    isRemainingPositive = remaining >= BigDecimal.ZERO
)