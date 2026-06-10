package com.kholodkov.coinmonitor.domain.usecase.transaction

import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class ObserveDailySummaryTest {

    private val today = LocalDate.of(2026, 1, 3)
    private val exchangeRates = ExchangeRates(
        listOf(
            ExchangeRate(
                date = LocalDate.of(2026, 1, 1),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("110")
            ),
            ExchangeRate(
                date = LocalDate.of(2026, 1, 2),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("115")
            ),
            ExchangeRate(
                date = LocalDate.of(2026, 1, 3),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("120")
            )
        )
    )

    private val appConfig = AppConfig(
        dailyLimit = BigDecimal("6000"),
        dailyLimitCurrency = Currency.RSD,
        startDate = LocalDate.of(2026, 1, 1),
        initialBalance = BigDecimal.ZERO
    )

    private val transactionRepository = mockk<TransactionRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val exchangeRepository = mockk<ExchangeRepository>()
    private val appConfigRepository = mockk<AppConfigRepository>()

    private val useCase = ObserveDailySummary(
        transactionRepository = transactionRepository,
        settingsRepository = settingsRepository,
        exchangeRepository = exchangeRepository,
        appConfigRepository = appConfigRepository
    )

    @Before
    fun setup() {
        every { exchangeRepository.observeExchangeRates() } returns flowOf(exchangeRates)
        every { settingsRepository.observeDisplayCurrency() } returns flowOf(Currency.RSD)
        every { appConfigRepository.observeConfig() } returns flowOf(appConfig)
    }

    @Test
    fun `budget equals daily limit times days when no transactions`() = runTest {
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(emptyList())
        val expectedBudget = BigDecimal("18000")
        val expectedSpent = BigDecimal("0")
        val expectedRemaining = BigDecimal("18000")
        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `budget reduces by spending before today`() = runTest {
        val transactions = listOf(
            fakeTransaction(
                amount = BigDecimal("500"),
                date = LocalDate.of(2026, 1, 1)
            ),
            fakeTransaction(
                amount = BigDecimal("4000"),
                date = LocalDate.of(2026, 1, 2)
            )
        )
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(transactions)
        val expectedBudget = BigDecimal("13500")
        val expectedSpent = BigDecimal("0")
        val expectedRemaining = BigDecimal("13500")

        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `today spending is excluded from budget but counted in spent and remaining`() = runTest {
        val transactions = listOf(
            fakeTransaction(
                amount = BigDecimal("500"),
                date = LocalDate.of(2026, 1, 3)
            ),
            fakeTransaction(
                amount = BigDecimal("4000"),
                date = LocalDate.of(2026, 1, 3)
            )
        )
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(transactions)
        val expectedBudget = BigDecimal("18000")
        val expectedSpent = BigDecimal("4500")
        val expectedRemaining = BigDecimal("13500")

        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `remaining is negative when spending exceeds budget`() = runTest {
        val transactions = listOf(
            fakeTransaction(
                amount = BigDecimal("10000"),
                date = LocalDate.of(2026, 1, 1)
            ),
            fakeTransaction(
                amount = BigDecimal("10000"),
                date = LocalDate.of(2026, 1, 3)
            )
        )
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(transactions)
        val expectedBudget = BigDecimal("8000")
        val expectedSpent = BigDecimal("10000")
        val expectedRemaining = BigDecimal("-2000")

        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `amounts are converted to display currency`() = runTest {
        every { settingsRepository.observeDisplayCurrency() } returns flowOf(Currency.EUR)

        val transactions = listOf(
            fakeTransaction(
                amount = BigDecimal("6000"),
                date = LocalDate.of(2026, 1, 1)
            ),
            fakeTransaction(
                amount = BigDecimal("4800"),
                date = LocalDate.of(2026, 1, 2)
            ),
            fakeTransaction(
                amount = BigDecimal("4800"),
                date = LocalDate.of(2026, 1, 3)
            )
        )
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(transactions)
        val expectedBudget = BigDecimal("60")
        val expectedSpent = BigDecimal("40")
        val expectedRemaining = BigDecimal("20")

        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `budget in EUR is converted correctly`() = runTest {
        every { appConfigRepository.observeConfig() } returns flowOf(
            appConfig.copy(
                dailyLimit = BigDecimal("50"),
                dailyLimitCurrency = Currency.EUR
            )
        )

        val transactions = listOf(
            fakeTransaction(
                amount = BigDecimal("5500"),
                date = LocalDate.of(2026, 1, 1)
            ),
            fakeTransaction(
                amount = BigDecimal("5750"),
                date = LocalDate.of(2026, 1, 2)
            ),
            fakeTransaction(
                amount = BigDecimal("6000"),
                date = LocalDate.of(2026, 1, 3)
            )
        )

        every { transactionRepository.observeUpToDate(any()) } returns flowOf(transactions)
        val expectedBudget = BigDecimal("6000")
        val expectedSpent = BigDecimal("6000")
        val expectedRemaining = BigDecimal("0")

        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `initial balance is added to budget`() = runTest {
        every { appConfigRepository.observeConfig() } returns flowOf(
            appConfig.copy(
                initialBalance = BigDecimal("10000")
            )
        )
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(emptyList())
        val expectedBudget = BigDecimal("28000")
        val expectedSpent = BigDecimal("0")
        val expectedRemaining = BigDecimal("28000")
        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    @Test
    fun `transactions in different currencies are summed`() = runTest {
        val transactions = listOf(
            fakeTransaction(
                amount = BigDecimal("5000"),
                date = LocalDate.of(2026, 1, 1)
            ),
            fakeTransaction(
                amount = BigDecimal("30"),
                date = LocalDate.of(2026, 1, 2),
                currency = Currency.EUR
            )
        )
        every { transactionRepository.observeUpToDate(any()) } returns flowOf(transactions)
        val expectedBudget = BigDecimal("9550")
        val expectedSpent = BigDecimal("0")
        val expectedRemaining = BigDecimal("9550")

        val result = useCase(today).single()

        assertThat(result.budget).isEqualByComparingTo(expectedBudget)
        assertThat(result.spent).isEqualByComparingTo(expectedSpent)
        assertThat(result.remaining).isEqualByComparingTo(expectedRemaining)
    }

    private fun fakeTransaction(
        amount: BigDecimal,
        currency: Currency = Currency.RSD,
        date: LocalDate
    ) = Transaction(
        uid = "uid",
        date = date,
        userUid = "user",
        amount = amount,
        currency = currency,
        time = LocalTime.NOON,
        updatedAt = Instant.EPOCH,
        user = "user"
    )
}