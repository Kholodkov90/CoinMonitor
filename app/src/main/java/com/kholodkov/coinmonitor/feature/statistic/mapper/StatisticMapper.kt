package com.kholodkov.coinmonitor.feature.statistic.mapper

import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.core.tools.toDisplayStringFloored
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.statistic.MonthStats
import com.kholodkov.coinmonitor.domain.model.statistic.WeekStats
import com.kholodkov.coinmonitor.domain.model.statistic.YearStats
import com.kholodkov.coinmonitor.feature.statistic.model.ui.StatisticItem
import java.time.format.DateTimeFormatter

fun WeekStats.toStatisticItem(currency: Currency) = StatisticItem(
    period = "${dateFrom.toDisplayString()} - ${dateTo.toDisplayString()}",
    totalSpent = "${totalSpent.toDisplayString()} ${currency.name}",
    transactionCount = transactionCount.toString(),
    average = "${average.toDisplayStringFloored()} ${currency.name}",
)

fun MonthStats.toStatisticItem(currency: Currency) = StatisticItem(
    period = monthStart.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
    totalSpent = "${totalSpent.toDisplayString()} ${currency.name}",
    transactionCount = transactionCount.toString(),
    average = "${average.toDisplayStringFloored()} ${currency.name}",
)

fun YearStats.toStatisticItem(currency: Currency) = StatisticItem(
    period = year.toString(),
    totalSpent = "${totalSpent.toDisplayString()} ${currency.name}",
    transactionCount = transactionCount.toString(),
    average = "${average.toDisplayStringFloored()} ${currency.name}",
)

fun List<WeekStats>.toWeekStatisticItems(currency: Currency) = map { it.toStatisticItem(currency) }
fun List<MonthStats>.toMonthStatisticItems(currency: Currency) = map { it.toStatisticItem(currency) }
fun List<YearStats>.toYearStatisticItems(currency: Currency) = map { it.toStatisticItem(currency) }