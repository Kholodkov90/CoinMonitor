package com.kholodkov.coinmonitor.domain.model.currency

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class ExchangeRatesTest {

    private val exchangeRates = ExchangeRates(
        rates = listOf(
            ExchangeRate(
                date = LocalDate.of(2026, 1, 1),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("117")
            ),
            ExchangeRate(
                date = LocalDate.of(2026, 1, 2),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("118")
            ),
            ExchangeRate(
                date = LocalDate.of(2026, 1, 3),
                currency = Currency.RSD,
                exchangeRate = BigDecimal("119")
            )
        )
    )


    @Test
    fun `convert from EUR to RSD`() {
        val amount = BigDecimal("10")
        val expected = BigDecimal("1170")

        val result = exchangeRates.convert(
            amount = amount,
            from = Currency.EUR,
            to = Currency.RSD,
            date = LocalDate.of(2026, 1, 1)
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `convert from RSD to EUR`() {
        val amount = BigDecimal("1170")
        val expected = BigDecimal("10")

        val result = exchangeRates.convert(
            amount = amount,
            from = Currency.RSD,
            to = Currency.EUR,
            date = LocalDate.of(2026, 1, 1)
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `convert same currency return same amount`() {
        val amount = BigDecimal("1170")

        val result = exchangeRates.convert(
            amount = amount,
            from = Currency.RSD,
            to = Currency.RSD,
            date = LocalDate.of(2026, 1, 1)
        )

        assertThat(result).isEqualByComparingTo(amount)
    }

    @Test
    fun `convert uses floor rate when there's no date in rate map`() {
        val amount = BigDecimal("1190")
        val expected = BigDecimal("10")

        val result = exchangeRates.convert(
            amount = amount,
            from = Currency.RSD,
            to = Currency.EUR,
            date = LocalDate.of(2026, 1, 4)
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `convert uses default rate when date is before first date in rate map`() {
        val amount = BigDecimal("1174")
        val expected = BigDecimal("10")

        val result = exchangeRates.convert(
            amount = amount,
            from = Currency.RSD,
            to = Currency.EUR,
            date = LocalDate.of(2025, 1, 1)
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `convert RSD to EUR and back without drift`() {
        val amount = BigDecimal("1000")
        val date = LocalDate.of(2026, 1, 1)
        val inEur = exchangeRates.convert(amount, Currency.RSD, Currency.EUR, date)
        val backToRsd = exchangeRates.convert(inEur, Currency.EUR, Currency.RSD, date)

        assertThat(backToRsd).isCloseTo(amount, within(BigDecimal("0.01")))
    }
}