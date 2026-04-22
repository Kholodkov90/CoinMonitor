package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.core.tools.fromDisplayDate
import com.kholodkov.coinmonitor.domain.model.Currency
import com.kholodkov.coinmonitor.domain.model.CurrencySum
import com.kholodkov.coinmonitor.domain.model.DailySummary
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import com.kholodkov.coinmonitor.domain.tools.BudgetDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject

class ObserveDailySummary @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val preferencesRepository: PreferencesRepository
) {
    private val startDate = BudgetDefaults.START_DATE.fromDisplayDate()

    operator fun invoke(date: LocalDate): Flow<DailySummary> {
        return combine(
            transactionRepository.observeSpendsBefore(date),
            transactionRepository.observeSpendsByDate(date),
            preferencesRepository.observeDisplayCurrency()
        ) { historicalSpends, dailySpends, currency ->
            val totalDays = date.toEpochDay() - startDate.toEpochDay() + 1
            val totalBalance = BigDecimal.valueOf(totalDays).multiply(BudgetDefaults.DAILY_LIMIT)
            val totalHistorical = historicalSpends.sumOf { it.convertTo(currency) }

            val availableBalance = totalBalance.subtract(totalHistorical)
            val spent = dailySpends.sumOf { it.convertTo(currency) }
            val remaining = availableBalance.subtract(spent)

            DailySummary(
                balance = availableBalance,
                spent = spent,
                remaining = remaining,
                currency = currency
            )
        }
    }

    private fun CurrencySum.convertTo(target: Currency): BigDecimal {
        return when (currency) {
            target -> amount
            Currency.EUR if target == Currency.RSD -> amount.multiply(BudgetDefaults.DEFAULT_RATE)

            Currency.RSD if target == Currency.EUR -> amount.divide(
                BudgetDefaults.DEFAULT_RATE,
                2,
                RoundingMode.HALF_UP
            )

            else -> error("Unsupported currency conversion")
        }
    }

}