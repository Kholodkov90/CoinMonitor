package com.kholodkov.coinmonitor.domain.model.currency

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.TreeMap

class ExchangeRates(rates: List<ExchangeRate>) {
    private val exchangeRates = TreeMap<LocalDate, BigDecimal>().apply {
        rates.forEach { put(it.date, it.exchangeRate) }
    }

    fun convert(
        amount: BigDecimal,
        from: Currency,
        to: Currency,
        date: LocalDate
    ): BigDecimal {
        if (from == to) return amount
        val inBase = convertToDefault(amount, from, date)
        return convertFromDefault(inBase, to, date)
    }

    private fun convertToDefault(
        amount: BigDecimal,
        from: Currency,
        date: LocalDate
    ): BigDecimal {
        if (from == DEFAULT_CURRENCY) return amount
        val rate = getRate(date)
        return amount.divide(rate, 10, RoundingMode.HALF_UP)
    }

    private fun convertFromDefault(
        amount: BigDecimal,
        to: Currency,
        date: LocalDate
    ): BigDecimal {
        if (to == DEFAULT_CURRENCY) return amount
        val rate = getRate(date)
        return amount.multiply(rate)
    }

    private fun getRate(date: LocalDate): BigDecimal =
        exchangeRates.floorKey(date)
            ?.let { exchangeRates.getValue(it) }
            ?: DEFAULT_RATE

    companion object {
        private val DEFAULT_CURRENCY = Currency.EUR
        private val DEFAULT_RATE = BigDecimal("117.4")
    }
}