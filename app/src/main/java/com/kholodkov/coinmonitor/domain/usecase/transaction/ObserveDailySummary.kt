package com.kholodkov.coinmonitor.domain.usecase.transaction

import com.kholodkov.coinmonitor.domain.model.transaction.DailySummary
import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import com.kholodkov.coinmonitor.domain.tools.calculateBudget
import com.kholodkov.coinmonitor.domain.tools.calculateSpentByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject

class ObserveDailySummary @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val settingsRepository: SettingsRepository,
    private val exchangeRepository: ExchangeRepository,
    private val appConfigRepository: AppConfigRepository
) {

    operator fun invoke(date: LocalDate): Flow<DailySummary> {
        return combine(
            transactionRepository.observeUpToDate(date),
            exchangeRepository.observeExchangeRates(),
            settingsRepository.observeDisplayCurrency(),
            appConfigRepository.observeConfig()
        ) { transactions,
            exchangeRates,
            displayCurrency,
            appConfig ->

            val spentByDate = transactions.calculateSpentByDate(
                exchangeRates = exchangeRates,
                targetCurrency = appConfig.dailyLimitCurrency
            )

            val spentBefore = spentByDate.filterKeys { it < date }.values.sumOf { it }
            val spentOnDay = spentByDate[date] ?: BigDecimal.ZERO

            val budget = calculateBudget(
                date = date,
                totalSpent = spentBefore,
                appConfig = appConfig
            )

            val remaining = budget.minus(spentOnDay)

            DailySummary(
                budget = exchangeRates.convert(
                    amount = budget,
                    from = appConfig.dailyLimitCurrency,
                    to = displayCurrency,
                    date = date
                ).setScale(2, RoundingMode.HALF_UP),
                spent = exchangeRates.convert(
                    amount = spentOnDay,
                    from = appConfig.dailyLimitCurrency,
                    to = displayCurrency,
                    date = date
                ).setScale(2, RoundingMode.HALF_UP),
                remaining = exchangeRates.convert(
                    amount = remaining,
                    from = appConfig.dailyLimitCurrency,
                    to = displayCurrency,
                    date = date
                ).setScale(2, RoundingMode.HALF_UP),
                currency = displayCurrency
            )
        }
    }
}