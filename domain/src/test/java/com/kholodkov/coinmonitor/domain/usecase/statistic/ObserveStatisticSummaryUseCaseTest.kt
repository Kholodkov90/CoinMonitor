package com.kholodkov.coinmonitor.domain.usecase.statistic

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.statistic.MonthStats
import com.kholodkov.coinmonitor.domain.model.statistic.WeekStats
import com.kholodkov.coinmonitor.domain.model.statistic.YearStats
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class ObserveStatisticSummaryUseCaseTest {

    private val exchangeRates = ExchangeRates(
        listOf(
            ExchangeRate(
                date = LocalDate.of(2026, 1, 1),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("117")
            ),
            ExchangeRate(
                date = LocalDate.of(2026, 1, 2),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("118")
            )
        )
    )

    // Thursday
    private val today = LocalDate.of(2026, 1, 8)  // Thursday

    private val thisWeekMonday = LocalDate.of(2026, 1, 5)
    private val prevWeekMonday = LocalDate.of(2025, 12, 29)
    private val twoWeeksAgoMonday = LocalDate.of(2025, 12, 22)

    private val thisMonthStart = LocalDate.of(2026, 1, 1)
    private val prevMonthStart = LocalDate.of(2025, 12, 1)
    private val twoMonthsAgoStart = LocalDate.of(2025, 11, 1)

    private val thisYearStart = LocalDate.of(2026, 1, 1)
    private val prevYearStart = LocalDate.of(2025, 1, 1)
    private val twoYearsAgoStart = LocalDate.of(2024, 1, 1)

    private val transactionRepository = mockk<TransactionRepository>()
    private val exchangeRepository = mockk<ExchangeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val useCase = ObserveStatisticSummaryUseCase(
        transactionRepository = transactionRepository,
        exchangeRepository = exchangeRepository,
        settingsRepository = settingsRepository,
        clock = Clock.fixed(
            today.atStartOfDay(ZoneOffset.UTC).toInstant(),
            ZoneOffset.UTC
        )
    )

    @Before
    fun setup() {
        every { exchangeRepository.observeExchangeRates() } returns flowOf(exchangeRates)
        every { settingsRepository.observeDisplayCurrency() } returns flowOf(Currency.RSD)
        every { settingsRepository.observeStartOfWeek() } returns flowOf(DayOfWeek.MONDAY)
    }

    @Test
    fun `empty transactions returns empty stats`() = runTest {
        every { transactionRepository.observeAll() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(emptyList<WeekStats>(), result.weeklyStats)
        assertEquals(emptyList<MonthStats>(), result.monthlyStats)
        assertEquals(emptyList<YearStats>(), result.yearlyStats)
    }

    @Test
    fun `display currency is set in summary`() = runTest {
        every { settingsRepository.observeDisplayCurrency() } returns flowOf(Currency.EUR)
        every { transactionRepository.observeAll() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(Currency.EUR, result.currency)
    }

    @Test
    fun `transaction count is correct in weekly monthly and yearly stats`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = thisWeekMonday, amount = BigDecimal("100")),
            fakeTransaction(date = thisWeekMonday.plusDays(1), amount = BigDecimal("100")),
            fakeTransaction(date = thisWeekMonday.plusDays(2), amount = BigDecimal("100")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()

        val week = result.weeklyStats.single { it.dateFrom == thisWeekMonday }
        val month = result.monthlyStats.single { it.monthStart == thisMonthStart }
        val year = result.yearlyStats.single { it.year == thisYearStart.year }

        assertEquals(3, week.transactionCount)
        assertEquals(3, month.transactionCount)
        assertEquals(3, year.transactionCount)
    }

    @Test
    fun `single week average counts days from first transaction to today`() = runTest {
        // All transactions are within the current week
        val transactions = listOf(
            fakeTransaction(date = thisWeekMonday.plusDays(1), amount = BigDecimal("1500")),
            fakeTransaction(date = thisWeekMonday.plusDays(1), amount = BigDecimal("1500"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("3000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val week = result.weeklyStats.single { it.dateFrom == thisWeekMonday }

        assertThat(week.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        // Monday is excluded because the first transaction ever is on Tuesday
        assertThat(week.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `first week counts days from first transaction to week end`() = runTest {
        // Counting starts on Tuesday, the first transaction's date
        val transactions = listOf(
            fakeTransaction(date = prevWeekMonday.plusDays(1), amount = BigDecimal("3000")),
            fakeTransaction(date = prevWeekMonday.plusDays(4), amount = BigDecimal("3000"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("6000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val week = result.weeklyStats.single { it.dateFrom == prevWeekMonday }

        assertThat(week.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(week.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `last week counts days from monday to today`() = runTest {
        // Counting last week from Monday to today (4 days)
        // The previous week transaction is only here so this week is not the first
        val transactions = listOf(
            fakeTransaction(
                date = prevWeekMonday,
                amount = BigDecimal("1000")
            ),
            fakeTransaction(
                date = thisWeekMonday.plusDays(1),
                amount = BigDecimal("4000")
            )
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("4000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val week = result.weeklyStats.single { it.dateFrom == thisWeekMonday }

        assertThat(week.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(week.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `full week average divides total by seven days`() = runTest {
        // Previous week is a middle week, so it counts as a full 7 days
        // The earlier transaction makes previous week not-first
        val transactions = listOf(
            fakeTransaction(date = twoWeeksAgoMonday, amount = BigDecimal("100")),
            fakeTransaction(
                date = prevWeekMonday.plusDays(2),
                amount = BigDecimal("7000")
            )
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("7000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val week = result.weeklyStats.single { it.dateFrom == prevWeekMonday }

        assertThat(week.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(week.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `week starts on Sunday when start of week is Sunday`() = runTest {
        every { settingsRepository.observeStartOfWeek() } returns flowOf(DayOfWeek.SUNDAY)
        val transactions = listOf(
            fakeTransaction(date = today, amount = BigDecimal("100")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()
        val week = result.weeklyStats.single()

        assertEquals(thisWeekMonday.minusDays(1), week.dateFrom)
    }

    @Test
    fun `empty week between transactions has zero sums`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoWeeksAgoMonday, amount = BigDecimal("100")),
            fakeTransaction(date = thisWeekMonday, amount = BigDecimal("100")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()
        val week = result.weeklyStats.single { it.dateFrom == prevWeekMonday }

        assertThat(week.totalSpent).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(week.average).isEqualByComparingTo(BigDecimal.ZERO)
    }

    @Test
    fun `weekly stats are sorted descending`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoWeeksAgoMonday, amount = BigDecimal("100")),
            fakeTransaction(date = prevWeekMonday, amount = BigDecimal("100")),
            fakeTransaction(date = thisWeekMonday, amount = BigDecimal("100"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()

        val dates = result.weeklyStats.map { it.dateFrom }
        assertEquals(listOf(thisWeekMonday, prevWeekMonday, twoWeeksAgoMonday), dates)
    }

    @Test
    fun `single month average counts days from first transaction to today`() = runTest {
        // All transactions are within the current month
        val transactions = listOf(
            fakeTransaction(date = today.minusDays(2), amount = BigDecimal("1500")),
            fakeTransaction(date = today.minusDays(1), amount = BigDecimal("1500")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("3000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val month = result.monthlyStats.single { it.monthStart == thisMonthStart }

        assertThat(month.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(month.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `first month average counts days from first transaction to month end`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = prevMonthStart.plusDays(5), amount = BigDecimal("10000")),
            fakeTransaction(date = prevMonthStart.plusDays(10), amount = BigDecimal("16000")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("26000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val month = result.monthlyStats.single { it.monthStart == prevMonthStart }

        assertThat(month.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(month.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `last month average counts days from month start to today`() = runTest {
        val transactions = listOf(
            fakeTransaction(
                date = prevMonthStart,
                amount = BigDecimal("1000")
            ),
            fakeTransaction(
                date = thisMonthStart.plusDays(1),
                amount = BigDecimal("8000")
            )
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("8000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val month = result.monthlyStats.single { it.monthStart == thisMonthStart }

        assertThat(month.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(month.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `full month average divides total by days in month`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoMonthsAgoStart, amount = BigDecimal("100")),
            fakeTransaction(
                date = prevMonthStart.plusDays(2),
                amount = BigDecimal("31000")
            )
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("31000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val month = result.monthlyStats.single { it.monthStart == prevMonthStart }

        assertThat(month.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(month.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `empty month between transactions has zero sums`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoMonthsAgoStart, amount = BigDecimal("100")),
            fakeTransaction(date = thisMonthStart, amount = BigDecimal("100")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()
        val month = result.monthlyStats.single { it.monthStart == prevMonthStart }

        assertThat(month.totalSpent).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(month.average).isEqualByComparingTo(BigDecimal.ZERO)
    }

    @Test
    fun `monthly stats are sorted descending`()= runTest {
        val transactions = listOf(
            fakeTransaction(date = twoMonthsAgoStart, amount = BigDecimal("100")),
            fakeTransaction(date = prevMonthStart, amount = BigDecimal("100")),
            fakeTransaction(date = thisMonthStart, amount = BigDecimal("100"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()

        val dates = result.monthlyStats.map { it.monthStart }
        assertEquals(listOf(thisMonthStart, prevMonthStart, twoMonthsAgoStart), dates)
    }

    @Test
    fun `single year average counts days from first transaction to today`() = runTest {
        // All transactions are within the current year
        val transactions = listOf(
            fakeTransaction(date = today.minusDays(2), amount = BigDecimal("1500")),
            fakeTransaction(date = today.minusDays(1), amount = BigDecimal("1500")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("3000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val year = result.yearlyStats.single { it.year == thisYearStart.year }

        assertThat(year.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(year.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `first year average counts days from first transaction to year end`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = prevYearStart.plusDays(5), amount = BigDecimal("10000")),
            fakeTransaction(date = prevYearStart.plusDays(10), amount = BigDecimal("350000")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("360000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val year = result.yearlyStats.single { it.year == prevYearStart.year }

        assertThat(year.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(year.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `last year average counts days from year start to today`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = prevYearStart, amount = BigDecimal("1000")),
            fakeTransaction(date = thisYearStart.plusDays(1), amount = BigDecimal("8000"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("8000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val year = result.yearlyStats.single { it.year == thisYearStart.year }

        assertThat(year.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(year.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `full year average divides total by days in year`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoYearsAgoStart, amount = BigDecimal("100")),
            fakeTransaction(date = prevYearStart.plusDays(2), amount = BigDecimal("365000"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val expectedTotalSpent = BigDecimal("365000")
        val expectedAverage = BigDecimal("1000")

        val result = useCase().first()
        val year = result.yearlyStats.single { it.year == prevYearStart.year }

        assertThat(year.totalSpent).isEqualByComparingTo(expectedTotalSpent)
        assertThat(year.average).isEqualByComparingTo(expectedAverage)
    }

    @Test
    fun `empty year between transactions has zero sums`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoYearsAgoStart, amount = BigDecimal("100")),
            fakeTransaction(date = thisYearStart, amount = BigDecimal("100")),
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()
        val year = result.yearlyStats.single { it.year == prevYearStart.year }

        assertThat(year.totalSpent).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(year.average).isEqualByComparingTo(BigDecimal.ZERO)
    }

    @Test
    fun `yearly stats are sorted descending`() = runTest {
        val transactions = listOf(
            fakeTransaction(date = twoYearsAgoStart, amount = BigDecimal("100")),
            fakeTransaction(date = prevYearStart, amount = BigDecimal("100")),
            fakeTransaction(date = thisYearStart, amount = BigDecimal("100"))
        )
        every { transactionRepository.observeAll() } returns flowOf(transactions)

        val result = useCase().first()

        val years = result.yearlyStats.map { it.year }
        assertEquals(listOf(thisYearStart.year, prevYearStart.year, twoYearsAgoStart.year), years)
    }

    private fun fakeTransaction(
        date: LocalDate,
        currency: Currency = Currency.RSD,
        amount: BigDecimal
    ) = Transaction(
        uid = "uid",
        date = date,
        userUid = "userUid",
        amount = amount,
        currency = currency,
        time = LocalTime.NOON,
        updatedAt = Instant.EPOCH,
        user = "user"
    )
}