package com.kholodkov.coinmonitor.domain.usecase.statistic

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.statistic.MonthStats
import com.kholodkov.coinmonitor.domain.model.statistic.StatisticSummary
import com.kholodkov.coinmonitor.domain.model.statistic.WeekStats
import com.kholodkov.coinmonitor.domain.model.statistic.YearStats
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import com.kholodkov.coinmonitor.domain.tools.calculateSpentByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.inject.Inject

class ObserveStatisticSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val exchangeRepository: ExchangeRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<StatisticSummary> = combine(
        transactionRepository.observeAll(),
        exchangeRepository.observeExchangeRates(),
        settingsRepository.observeDisplayCurrency(),
        settingsRepository.observeStartOfWeek()
    ) { transactions, exchangeRates, currency, startOfWeek ->
        StatisticSummary(
            currency = currency,
            weeklyStats = transactions.groupByWeek(
                exchangeRates = exchangeRates,
                currency = currency,
                startOfWeek = startOfWeek
            ),
            monthlyStats = transactions.groupByMonth(
                exchangeRates = exchangeRates,
                currency = currency
            ),
            yearlyStats = transactions.groupByYear(
                exchangeRates = exchangeRates,
                currency = currency
            )
        )
    }

    private fun List<Transaction>.groupByWeek(
        exchangeRates: ExchangeRates,
        currency: Currency,
        startOfWeek: DayOfWeek
    ): List<WeekStats> {
        if (isEmpty()) return emptyList()

        val weekFields = WeekFields.of(startOfWeek, 1)
        val firstDate = minOf { it.date }
        val firstStartOfWeek = firstDate.with(weekFields.dayOfWeek(), 1L)
        val lastStartOfWeek = LocalDate.now().with(weekFields.dayOfWeek(), 1L)

        val transactionsByWeek = groupBy {
            it.date.with(weekFields.dayOfWeek(), 1L)
        }

        return generateSequence(firstStartOfWeek) { it.plusWeeks(1) }
            .takeWhile { it <= lastStartOfWeek }
            .map { startDay ->
                val weekTransactions = transactionsByWeek[startDay] ?: emptyList()

                val totalSpent = weekTransactions.calculateSpentByDate(
                    exchangeRates = exchangeRates,
                    currency = currency
                ).values.sumOf { it }

                val daysInWeek = when (startDay) {
                    firstStartOfWeek -> ChronoUnit.DAYS.between(firstDate, startDay.plusDays(7))
                    lastStartOfWeek -> ChronoUnit.DAYS.between(startDay, LocalDate.now()) + 1
                    else -> 7L
                }

                val average = totalSpent.divide(daysInWeek.toBigDecimal(), 2, RoundingMode.HALF_UP)

                WeekStats(
                    dateFrom = startDay,
                    dateTo = startDay.plusDays(6),
                    transactionCount = weekTransactions.size,
                    totalSpent = totalSpent,
                    average = average
                )
            }
            .toList()
            .sortedByDescending { it.dateFrom }
    }

    private fun List<Transaction>.groupByMonth(
        exchangeRates: ExchangeRates,
        currency: Currency
    ): List<MonthStats> {
        if (isEmpty()) return emptyList()

        val firstDate = minOf { it.date }
        val firstMonth = firstDate.withDayOfMonth(1)
        val lastMonth = LocalDate.now().withDayOfMonth(1)

        val transactionsByMonth = groupBy { it.date.withDayOfMonth(1) }

        return generateSequence(firstMonth) { it.plusMonths(1) }
            .takeWhile { it <= lastMonth }
            .map { monthStart ->
                val monthTransactions = transactionsByMonth[monthStart] ?: emptyList()

                val totalSpent = monthTransactions.calculateSpentByDate(
                    exchangeRates = exchangeRates,
                    currency = currency
                ).values.sumOf { it }

                val daysInMonth = when (monthStart) {
                    firstMonth -> ChronoUnit.DAYS.between(firstDate, monthStart.plusMonths(1))
                    lastMonth -> ChronoUnit.DAYS.between(monthStart, LocalDate.now()) + 1
                    else -> monthStart.lengthOfMonth().toLong()
                }

                val average = totalSpent.divide(daysInMonth.toBigDecimal(), 2, RoundingMode.HALF_UP)

                MonthStats(
                    monthStart = monthStart,
                    transactionCount = monthTransactions.size,
                    totalSpent = totalSpent,
                    average = average
                )
            }
            .toList()
            .sortedByDescending { it.monthStart }
    }

    private fun List<Transaction>.groupByYear(
        exchangeRates: ExchangeRates,
        currency: Currency
    ): List<YearStats> {
        if (isEmpty()) return emptyList()

        val firstDate = minOf { it.date }
        val firstYearStart = firstDate.withDayOfYear(1)
        val lastYearStart = LocalDate.now().withDayOfYear(1)

        val transactionsByYear = groupBy { it.date.withDayOfYear(1) }

        return generateSequence(firstYearStart) { it.plusYears(1) }
            .takeWhile { it <= lastYearStart }
            .map { yearStart ->
                val yearTransactions = transactionsByYear[yearStart] ?: emptyList()

                val totalSpent = yearTransactions.calculateSpentByDate(
                    exchangeRates = exchangeRates,
                    currency = currency
                ).values.sumOf { it }

                val daysInYear = when (yearStart) {
                    firstYearStart -> ChronoUnit.DAYS.between(firstDate, yearStart.plusYears(1))
                    lastYearStart -> ChronoUnit.DAYS.between(yearStart, LocalDate.now()) + 1
                    else -> if (yearStart.isLeapYear) 366L else 365L
                }

                YearStats(
                    year = yearStart.year,
                    transactionCount = yearTransactions.size,
                    totalSpent = totalSpent,
                    average = totalSpent.divide(daysInYear.toBigDecimal(), 2, RoundingMode.HALF_UP)
                )
            }
            .toList()
            .sortedByDescending { it.year }
    }
}