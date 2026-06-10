package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class SpentCalculatorTest {

    private val exchangeRates = ExchangeRates(
        listOf(
            ExchangeRate(
                date = LocalDate.of(2026, 1, 1),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("117")
            )
        )
    )

    @Test
    fun `empty list returns empty map`() {
        val result = emptyList<Transaction>().calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = Currency.EUR
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun `single EUR transaction returns same amount`() {
        val date = LocalDate.of(2026, 1, 1)
        val amount = BigDecimal("10")
        val transactions = listOf(
            fakeTransaction(
                amount = amount,
                currency = Currency.EUR,
                date = date
            )
        )

        val result = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = Currency.EUR
        )

        assertThat(result[date]).isEqualByComparingTo(amount)
    }

    @Test
    fun `single RSD transaction converts to EUR`() {
        val date = LocalDate.of(2026, 1, 1)
        val amount = BigDecimal("1170")
        val transactions = listOf(
            fakeTransaction(
                amount = amount,
                currency = Currency.RSD,
                date = date
            )
        )
        val expected = BigDecimal("10")

        val result = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = Currency.EUR
        )

        assertThat(result[date]).isEqualByComparingTo(expected)
    }

    @Test
    fun `multiple transactions same currency are summed`() {
        val date = LocalDate.of(2026, 1, 1)
        val amount = BigDecimal("10")
        val transactions = listOf(
            fakeTransaction(
                amount = amount,
                currency = Currency.EUR,
                date = date
            ),
            fakeTransaction(
                amount = amount,
                currency = Currency.EUR,
                date = date
            )
        )
        val expected = BigDecimal("20")

        val result = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = Currency.EUR
        )

        assertThat(result[date]).isEqualByComparingTo(expected)
    }

    @Test
    fun `transactions on different dates are grouped separately`() {
        val date1 = LocalDate.of(2026, 1, 1)
        val date2 = LocalDate.of(2026, 1, 2)
        val amount1 = BigDecimal("10")
        val amount2 = BigDecimal("20")
        val transactions = listOf(
            fakeTransaction(
                amount = amount1,
                currency = Currency.EUR,
                date = date1
            ),
            fakeTransaction(
                amount = amount1,
                currency = Currency.EUR,
                date = date1
            ),
            fakeTransaction(
                amount = amount2,
                currency = Currency.EUR,
                date = date2
            ),
            fakeTransaction(
                amount = amount2,
                currency = Currency.EUR,
                date = date2
            )
        )
        val expected1 = BigDecimal("20")
        val expected2 = BigDecimal("40")

        val result = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = Currency.EUR
        )

        assertThat(result[date1]).isEqualByComparingTo(expected1)
        assertThat(result[date2]).isEqualByComparingTo(expected2)
    }

    @Test
    fun `mixed currencies on same day are converted and summed`() {
        val date = LocalDate.of(2026, 1, 1)
        val amountInRSD = BigDecimal("1500")
        val amountInEUR = BigDecimal("10")
        val transactions = listOf(
            fakeTransaction(
                amount = amountInRSD,
                currency = Currency.RSD,
                date = date
            ),
            fakeTransaction(
                amount = amountInEUR,
                currency = Currency.EUR,
                date = date
            )
        )
        val expected = BigDecimal("2670")

        val result = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = Currency.RSD
        )

        assertThat(result[date]).isEqualByComparingTo(expected)
    }

    private fun fakeTransaction(
        amount: BigDecimal,
        currency: Currency,
        date: LocalDate
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