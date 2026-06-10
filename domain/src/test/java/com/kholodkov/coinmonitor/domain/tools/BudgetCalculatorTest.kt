package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class BudgetCalculatorTest {

    private val startDate = LocalDate.of(2026, 1, 1)
    private val dailyLimit = BigDecimal("6000")
    private val appConfig = AppConfig(
        dailyLimit = dailyLimit,
        dailyLimitCurrency = Currency.RSD,
        startDate = startDate,
        initialBalance = BigDecimal.ZERO
    )

    @Test
    fun `budget on first day equals daily limit`() {
        val result = calculateBudget(
            date = startDate,
            totalSpent = BigDecimal.ZERO,
            appConfig = appConfig
        )

        assertThat(result).isEqualByComparingTo(dailyLimit)
    }

    @Test
    fun `budget accumulates over multiple days`() {
        val expected = BigDecimal("18000")

        val result = calculateBudget(
            date = startDate.plusDays(2),
            totalSpent = BigDecimal.ZERO,
            appConfig = appConfig
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `budget subtracts total spent`() {
        val totalSpent = BigDecimal("3000")
        val expected = BigDecimal("15000")

        val result = calculateBudget(
            date = startDate.plusDays(2),
            totalSpent = totalSpent,
            appConfig = appConfig
        )

        assertThat(result).isEqualByComparingTo(expected)
    }


    @Test
    fun `budget includes initial balance`() {
        val configWithBalance = appConfig.copy(initialBalance = BigDecimal("10000"))
        val expected = BigDecimal("28000")

        val result = calculateBudget(
            date = startDate.plusDays(2),
            totalSpent = BigDecimal.ZERO,
            appConfig = configWithBalance
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `budget is zero when date is before start date`() {
        val expected = BigDecimal.ZERO

        val result = calculateBudget(
            date = startDate.minusDays(1),
            totalSpent = BigDecimal.ZERO,
            appConfig = appConfig
        )

        assertThat(result).isEqualByComparingTo(expected)
    }

    @Test
    fun `budget is negative when spent is over than accumulated`() {
        val spent = BigDecimal("7000")
        val expected = BigDecimal("-1000")

        val result = calculateBudget(
            date = startDate,
            totalSpent = spent,
            appConfig = appConfig
        )

        assertThat(result).isEqualByComparingTo(expected)
    }
}