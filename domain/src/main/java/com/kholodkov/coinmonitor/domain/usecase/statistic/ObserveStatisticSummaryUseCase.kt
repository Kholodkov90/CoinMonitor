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
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.inject.Inject

class ObserveStatisticSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val exchangeRepository: ExchangeRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock
) {
    operator fun invoke(): Flow<StatisticSummary> = combine(
        transactionRepository.observeAll(),
        exchangeRepository.observeExchangeRates(),
        settingsRepository.observeDisplayCurrency(),
        settingsRepository.observeStartOfWeek()
    ) { transactions, exchangeRates, displayCurrency, startOfWeek ->

        val today = LocalDate.now(clock)

        StatisticSummary(
            currency = displayCurrency,
            weeklyStats = transactions.groupByWeek(
                exchangeRates = exchangeRates,
                displayCurrency = displayCurrency,
                startOfWeek = startOfWeek,
                today = today
            ),
            monthlyStats = transactions.groupByMonth(
                exchangeRates = exchangeRates,
                displayCurrency = displayCurrency,
                today = today
            ),
            yearlyStats = transactions.groupByYear(
                exchangeRates = exchangeRates,
                displayCurrency = displayCurrency,
                today = today
            )
        )
    }

    private fun List<Transaction>.groupByWeek(
        exchangeRates: ExchangeRates,
        displayCurrency: Currency,
        startOfWeek: DayOfWeek,
        today: LocalDate
    ): List<WeekStats> {
        if (isEmpty()) return emptyList()

        val weekFields = WeekFields.of(startOfWeek, 1)
        val firstDate = minOf { it.date }
        val firstStartOfWeek = firstDate.with(weekFields.dayOfWeek(), 1L)
        val lastStartOfWeek = today.with(weekFields.dayOfWeek(), 1L)

        val transactionsByWeek = groupBy {
            it.date.with(weekFields.dayOfWeek(), 1L)
        }

        return generateSequence(firstStartOfWeek) { it.plusWeeks(1) }
            .takeWhile { it <= lastStartOfWeek }
            .map { startDay ->
                val weekTransactions = transactionsByWeek[startDay] ?: emptyList()

                val totalSpent = weekTransactions.calculateSpentByDate(
                    exchangeRates = exchangeRates,
                    targetCurrency = displayCurrency
                )
                    .values
                    .sumOf { it }

                val daysInWeek = when (startDay) {
                    firstStartOfWeek if startDay == lastStartOfWeek -> ChronoUnit.DAYS.between(
                        firstDate,
                        today
                    ) + 1

                    firstStartOfWeek -> ChronoUnit.DAYS.between(firstDate, startDay.plusDays(7))
                    lastStartOfWeek -> ChronoUnit.DAYS.between(startDay, today) + 1

                    else -> 7L
                }
                val average = totalSpent.divide(daysInWeek.toBigDecimal(), 2, RoundingMode.HALF_UP)

                WeekStats(
                    dateFrom = startDay,
                    dateTo = startDay.plusDays(6),
                    transactionCount = weekTransactions.size,
                    totalSpent = totalSpent.setScale(2, RoundingMode.HALF_UP),
                    average = average
                )
            }
            .toList()
            .sortedByDescending { it.dateFrom }
    }

    private fun List<Transaction>.groupByMonth(
        exchangeRates: ExchangeRates,
        displayCurrency: Currency,
        today: LocalDate
    ): List<MonthStats> {
        if (isEmpty()) return emptyList()

        val firstDate = minOf { it.date }
        val firstMonth = firstDate.withDayOfMonth(1)
        val lastMonth = today.withDayOfMonth(1)

        val transactionsByMonth = groupBy { it.date.withDayOfMonth(1) }

        return generateSequence(firstMonth) { it.plusMonths(1) }
            .takeWhile { it <= lastMonth }
            .map { monthStart ->
                val monthTransactions = transactionsByMonth[monthStart] ?: emptyList()

                val totalSpent = monthTransactions.calculateSpentByDate(
                    exchangeRates = exchangeRates,
                    targetCurrency = displayCurrency
                ).values.sumOf { it }

                val daysInMonth = when (monthStart) {
                    firstMonth if monthStart == lastMonth -> ChronoUnit.DAYS.between(
                        firstDate,
                        today
                    ) + 1

                    firstMonth -> ChronoUnit.DAYS.between(firstDate, monthStart.plusMonths(1))
                    lastMonth -> ChronoUnit.DAYS.between(monthStart, today) + 1
                    else -> monthStart.lengthOfMonth().toLong()
                }

                val average = totalSpent.divide(daysInMonth.toBigDecimal(), 2, RoundingMode.HALF_UP)

                MonthStats(
                    monthStart = monthStart,
                    transactionCount = monthTransactions.size,
                    totalSpent = totalSpent.setScale(2, RoundingMode.HALF_UP),
                    average = average
                )
            }
            .toList()
            .sortedByDescending { it.monthStart }
    }

    private fun List<Transaction>.groupByYear(
        exchangeRates: ExchangeRates,
        displayCurrency: Currency,
        today: LocalDate
    ): List<YearStats> {
        if (isEmpty()) return emptyList()

        val firstDate = minOf { it.date }
        val firstYearStart = firstDate.withDayOfYear(1)
        val lastYearStart = today.withDayOfYear(1)

        val transactionsByYear = groupBy { it.date.withDayOfYear(1) }

        return generateSequence(firstYearStart) { it.plusYears(1) }
            .takeWhile { it <= lastYearStart }
            .map { yearStart ->
                val yearTransactions = transactionsByYear[yearStart] ?: emptyList()

                val totalSpent = yearTransactions.calculateSpentByDate(
                    exchangeRates = exchangeRates,
                    targetCurrency = displayCurrency
                ).values.sumOf { it }

                val daysInYear = when (yearStart) {
                    firstYearStart if yearStart == lastYearStart -> ChronoUnit.DAYS.between(
                        firstDate,
                        today
                    ) + 1

                    firstYearStart -> ChronoUnit.DAYS.between(firstDate, yearStart.plusYears(1))
                    lastYearStart -> ChronoUnit.DAYS.between(yearStart, today) + 1
                    else -> if (yearStart.isLeapYear) 366L else 365L
                }

                YearStats(
                    year = yearStart.year,
                    transactionCount = yearTransactions.size,
                    totalSpent = totalSpent.setScale(2, RoundingMode.HALF_UP),
                    average = totalSpent.divide(daysInYear.toBigDecimal(), 2, RoundingMode.HALF_UP)
                )
            }
            .toList()
            .sortedByDescending { it.year }
    }
}