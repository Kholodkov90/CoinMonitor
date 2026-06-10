package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.purchase.Purchase
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class ObservePurchasesUseCaseTest {

    private val today = LocalDate.of(2026, 1, 1)
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

    private val appConfig = AppConfig(
        dailyLimit = BigDecimal("6000"),
        dailyLimitCurrency = Currency.RSD,
        startDate = today,
        initialBalance = BigDecimal.ZERO
    )

    private val purchaseRepository = mockk<PurchaseRepository>()
    private val transactionRepository = mockk<TransactionRepository>()
    private val exchangeRepository = mockk<ExchangeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val appConfigRepository = mockk<AppConfigRepository>()

    private val useCase = ObservePurchasesUseCase(
        purchaseRepository = purchaseRepository,
        transactionRepository = transactionRepository,
        exchangeRepository = exchangeRepository,
        settingsRepository = settingsRepository,
        appConfigRepository = appConfigRepository,
        clock = Clock.fixed(
            today.atStartOfDay(ZoneOffset.UTC).toInstant(),
            ZoneOffset.UTC
        )
    )

    @Before
    fun setup() {
        every { transactionRepository.observeAll() } returns flowOf(emptyList())
        every { exchangeRepository.observeExchangeRates() } returns flowOf(exchangeRates)
        every { settingsRepository.observeDisplayCurrency() } returns flowOf(Currency.RSD)
        every { appConfigRepository.observeConfig() } returns flowOf(appConfig)
    }

    @Test
    fun `purchase with transactionUid is Bought even when amount fits now`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("6000"),
                    date = today,
                    transactionUid = "transactionUid"
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Bought, result.first().status)
    }

    @Test
    fun `purchase with transactionUid is Bought even when amount fits by date`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("12000"),
                    date = today.plusDays(1),
                    transactionUid = "transactionUid"
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Bought, result.first().status)
    }

    @Test
    fun `purchase with transactionUid is Bought even when amount doesn't fit by date`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("20000"),
                    date = today.plusDays(1),
                    transactionUid = "transactionUid"
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Bought, result.first().status)
    }

    @Test
    fun `purchase with transactionUid is Bought even when date is in past`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("20000"),
                    date = today.minusDays(1),
                    transactionUid = "transactionUid"
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Bought, result.first().status)
    }

    @Test
    fun `purchase is Available when amount fits now`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("6000"),
                    date = today,
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Available, result.first().status)
    }

    @Test
    fun `purchase is Available when amount fits now even if date is in past`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("6000"),
                    date = today.minusDays(1),
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Available, result.first().status)
    }

    @Test
    fun `purchase is Available when amount fits now even if date is in future`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("6000"),
                    date = today.plusDays(1),
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Available, result.first().status)
    }

    @Test
    fun `purchase is Overdue when date is in past and funds insufficient`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("20000"),
                    date = today.minusDays(1),
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Overdue, result.first().status)
    }

    @Test
    fun `purchase is Planned with correct daily limit when reachable by future date`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("10000"),
                    date = today.plusDays(1),
                )
            )
        )

        val result = useCase().first()

        val status = result.first().status
        assertThat(status).isInstanceOf(PurchaseStatus.Planned::class.java)

        status as PurchaseStatus.Planned
        assertThat(status.dailyLimit).isEqualByComparingTo(BigDecimal("2000"))
        assertEquals(Currency.RSD, status.currency)
    }

    @Test
    fun `purchase is Unreachable with correct gap when date is today and funds insufficient`() =
        runTest {
            every { purchaseRepository.observeAll() } returns flowOf(
                listOf(
                    fakePurchase(
                        amount = BigDecimal("20000"),
                        date = today,
                    )
                )
            )

            val result = useCase().first()

            val status = result.first().status
            assertThat(status).isInstanceOf(PurchaseStatus.Unreachable::class.java)

            status as PurchaseStatus.Unreachable
            assertThat(status.gap).isEqualByComparingTo(BigDecimal("14000"))
            assertEquals(Currency.RSD, status.currency)
        }

    @Test
    fun `purchase is Unreachable with correct gap when funds insufficient even by future date`() =
        runTest {
            every { purchaseRepository.observeAll() } returns flowOf(
                listOf(
                    fakePurchase(
                        amount = BigDecimal("20000"),
                        date = today.plusDays(1),
                    )
                )
            )

            val result = useCase().first()

            val status = result.first().status
            assertThat(status).isInstanceOf(PurchaseStatus.Unreachable::class.java)

            status as PurchaseStatus.Unreachable
            assertThat(status.gap).isEqualByComparingTo(BigDecimal("8000"))
            assertEquals(Currency.RSD, status.currency)
        }

    @Test
    fun `previous purchase reserves budget for next purchase`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("6000"),
                    date = today
                ),
                fakePurchase(
                    amount = BigDecimal("6000"),
                    date = today
                )
            )
        )

        val result = useCase().first()
        val firstPurchase = result[0]
        val secondPurchase = result[1]

        assertEquals(PurchaseStatus.Available, firstPurchase.status)
        assertThat(secondPurchase.status).isInstanceOf(PurchaseStatus.Unreachable::class.java)
    }

    @Test
    fun `purchases in different currencies get correct statuses`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("30"),
                    date = today,
                    currency = Currency.EUR
                ),
                fakePurchase(
                    amount = BigDecimal("3000"),
                    date = today,
                    currency = Currency.RSD
                )
            )
        )

        val result = useCase().first()

        assertEquals(PurchaseStatus.Available, result[0].status)
        assertThat(result[1].status).isInstanceOf(PurchaseStatus.Unreachable::class.java)
    }

    @Test
    fun `Planned daily limit is calculated in budget currency for EUR purchase`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("100"),
                    date = today.plusDays(1),
                    currency = Currency.EUR
                )
            )
        )

        val expectedDailyLimit = BigDecimal("200")

        val result = useCase().first()
        val status = result.first().status

        assertThat(status).isInstanceOf(PurchaseStatus.Planned::class.java)

        status as PurchaseStatus.Planned
        assertThat(status.dailyLimit).isEqualByComparingTo(expectedDailyLimit)
        assertEquals(Currency.RSD, status.currency)
    }

    @Test
    fun `Unreachable gap is calculated in budget currency for EUR purchase`() = runTest {
        every { purchaseRepository.observeAll() } returns flowOf(
            listOf(
                fakePurchase(
                    amount = BigDecimal("120"),
                    date = today.plusDays(1),
                    currency = Currency.EUR
                )
            )
        )

        val expectedGap = BigDecimal("2160")

        val result = useCase().first()
        val status = result.first().status

        assertThat(status).isInstanceOf(PurchaseStatus.Unreachable::class.java)

        status as PurchaseStatus.Unreachable
        assertThat(status.gap).isEqualByComparingTo(expectedGap)
        assertEquals(Currency.RSD, status.currency)
    }

    private fun fakePurchase(
        amount: BigDecimal,
        date: LocalDate,
        currency: Currency = Currency.RSD,
        transactionUid: String? = null
    ) = Purchase(
        uid = "uid",
        date = date,
        userUid = "user",
        amount = amount,
        transactionUid = transactionUid,
        currency = currency,
        description = "description",
        userName = "userName",
        updatedAt = Instant.EPOCH
    )
}