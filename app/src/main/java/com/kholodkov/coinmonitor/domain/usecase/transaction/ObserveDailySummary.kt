package com.kholodkov.coinmonitor.domain.usecase.transaction

import com.kholodkov.coinmonitor.domain.model.transaction.DailySummary
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import com.kholodkov.coinmonitor.domain.tools.calculateBudget
import com.kholodkov.coinmonitor.domain.tools.calculateSpentByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ObserveDailySummary @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val preferencesRepository: PreferencesRepository,
    private val exchangeRepository: ExchangeRepository,
) {

    operator fun invoke(date: LocalDate): Flow<DailySummary> {
        return combine(
            transactionRepository.observeUpToDate(date),
            exchangeRepository.observeExchangeRates(),
            preferencesRepository.observeDisplayCurrency()
        ) { transactions, exchangeRates, currency ->
            val spentByDate = transactions.calculateSpentByDate(
                exchangeRates = exchangeRates,
                currency = currency
            )

            val spentBefore = spentByDate.filterKeys { it < date }.values.sumOf { it }
            val spentOnDay = spentByDate[date] ?: BigDecimal.ZERO

            val budget = calculateBudget(
                date = date,
                exchangeRates = exchangeRates,
                currency = currency,
                totalSpent = spentBefore
            )

            val remaining = budget.minus(spentOnDay)

            DailySummary(
                budget = budget,
                spent = spentOnDay,
                remaining = remaining,
                currency = currency
            )
        }
    }
}