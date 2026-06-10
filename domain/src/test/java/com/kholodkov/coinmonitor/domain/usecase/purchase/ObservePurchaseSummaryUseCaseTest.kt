package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.purchase.PlannedPurchase
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
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
import java.time.LocalDate

class ObservePurchaseSummaryUseCaseTest {

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

    private val purchaseRepository = mockk<PurchaseRepository>()
    private val exchangeRepository = mockk<ExchangeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val useCase = ObservePurchaseSummaryUseCase(
        purchaseRepository,
        exchangeRepository,
        settingsRepository
    )

    @Before
    fun setup() {
        every { exchangeRepository.observeExchangeRates() } returns flowOf(exchangeRates)
        every { settingsRepository.observeDisplayCurrency() } returns flowOf(Currency.RSD)
    }

    @Test
    fun `empty list returns zero total`() = runTest {
        every { purchaseRepository.observePlanned() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(BigDecimal.ZERO, result.totalAmount)
        assertEquals(Currency.RSD, result.currency)
    }

    @Test
    fun `single RSD purchase returns same amount`() = runTest {
        val amount = BigDecimal("10000")

        every { purchaseRepository.observePlanned() } returns flowOf(listOf(
            PlannedPurchase(
                amount = amount,
                currency = Currency.RSD,
                date = LocalDate.of(2026, 1, 1)
            )
        ))

        val result = useCase().first()

        assertThat(result.totalAmount).isEqualByComparingTo(amount)
        assertEquals(Currency.RSD, result.currency)
    }

    @Test
    fun `single EUR purchase converts to RSD`() = runTest {
        val amount = BigDecimal("100")
        val expected = BigDecimal("11700")

        every { purchaseRepository.observePlanned() } returns flowOf(listOf(
            PlannedPurchase(
                amount = amount,
                currency = Currency.EUR,
                date = LocalDate.of(2026, 1, 1)
            )
        ))

        val result = useCase().first()

        assertThat(result.totalAmount).isEqualByComparingTo(expected)
        assertEquals(Currency.RSD, result.currency)
    }

    @Test
    fun `multiple purchases in different currencies are summed`() = runTest {
        val amountInRSD = BigDecimal("1000")
        val amountInEUR = BigDecimal("100")
        val expected = BigDecimal("12700")

        every { purchaseRepository.observePlanned() } returns flowOf(listOf(
            PlannedPurchase(
                amount = amountInRSD,
                currency = Currency.RSD,
                date = LocalDate.of(2026, 1, 1)
            ),
            PlannedPurchase(
                amount = amountInEUR,
                currency = Currency.EUR,
                date = LocalDate.of(2026, 1, 1)
            )
        ))

        val result = useCase().first()

        assertThat(result.totalAmount).isEqualByComparingTo(expected)
        assertEquals(Currency.RSD, result.currency)
    }

    @Test
    fun `multiple purchases in different days are summed`() = runTest {
        val amount = BigDecimal("100")
        val expected = BigDecimal("23500")

        every { purchaseRepository.observePlanned() } returns flowOf(listOf(
            PlannedPurchase(
                amount = amount,
                currency = Currency.EUR,
                date = LocalDate.of(2026, 1, 1)
            ),
            PlannedPurchase(
                amount = amount,
                currency = Currency.EUR,
                date = LocalDate.of(2026, 1, 2)
            ),
        ))

        val result = useCase().first()

        assertThat(result.totalAmount).isEqualByComparingTo(expected)
        assertEquals(Currency.RSD, result.currency)
    }
}