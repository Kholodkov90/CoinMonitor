package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.purchase.Purchase
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.domain.model.purchase.toProjection
import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
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

class ObservePurchasesUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val transactionRepository: TransactionRepository,
    private val exchangeRepository: ExchangeRepository,
    private val settingsRepository: SettingsRepository,
    private val appConfigRepository: AppConfigRepository
) {

    operator fun invoke(): Flow<List<PurchaseProjection>> = combine(
        purchaseRepository.observeAll(),
        transactionRepository.observeAll(),
        exchangeRepository.observeExchangeRates(),
        settingsRepository.observeDisplayCurrency(),
        appConfigRepository.observeConfig()
    ) { purchases,
        transactions,
        exchangeRates,
        displayCurrency,
        appConfig ->

        val spentByDate = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            targetCurrency = appConfig.dailyLimitCurrency
        )

        purchases.fold(emptyList()) { handledPurchases, purchase ->
            val purchaseDate =
                if (purchase.date < LocalDate.now()) LocalDate.now() else purchase.date
            val availableAmountToPurchaseDate = calculateAvailableAmount(
                date = purchaseDate,
                exchangeRates = exchangeRates,
                spentByDate = spentByDate,
                handledPurchases = handledPurchases,
                appConfig = appConfig
            )

            val availableAmountNow = if (purchaseDate == LocalDate.now()) {
                availableAmountToPurchaseDate
            } else {
                calculateAvailableAmount(
                    date = LocalDate.now(),
                    exchangeRates = exchangeRates,
                    spentByDate = spentByDate,
                    handledPurchases = handledPurchases,
                    appConfig = appConfig
                )
            }

            handledPurchases + buildProjection(
                purchase = purchase,
                availableAmountToPurchaseDate = availableAmountToPurchaseDate,
                availableAmountNow = availableAmountNow,
                displayCurrency = displayCurrency,
                exchangeRates = exchangeRates,
                appConfig = appConfig
            )
        }
    }

    private fun calculateAvailableAmount(
        date: LocalDate,
        exchangeRates: ExchangeRates,
        spentByDate: Map<LocalDate, BigDecimal>,
        handledPurchases: List<PurchaseProjection>,
        appConfig: AppConfig
    ): BigDecimal {
        val spentBefore = spentByDate.filter { it.key <= date }
            .values
            .sumOf { it }

        val plannedBefore = handledPurchases
            .filter { it.status !is PurchaseStatus.Bought }
            .fold(BigDecimal.ZERO) { plannedBefore, purchase ->
                plannedBefore + exchangeRates.convert(
                    amount = purchase.amount,
                    from = purchase.currency,
                    to = appConfig.dailyLimitCurrency,
                    date = purchase.date
                )
            }

        val totalSpent = spentBefore + plannedBefore

        return calculateBudget(
            date = date,
            totalSpent = totalSpent,
            appConfig = appConfig
        )
    }

    private fun buildProjection(
        purchase: Purchase,
        availableAmountToPurchaseDate: BigDecimal,
        availableAmountNow: BigDecimal,
        displayCurrency: Currency,
        exchangeRates: ExchangeRates,
        appConfig: AppConfig
    ): PurchaseProjection {
        if (purchase.transactionUid != null) {
            return purchase.toProjection(status = PurchaseStatus.Bought)
        }

        val amount = exchangeRates.convert(
            amount = purchase.amount,
            from = purchase.currency,
            to = appConfig.dailyLimitCurrency,
            date = purchase.date
        )

        if (amount <= availableAmountNow) {
            return purchase.toProjection(status = PurchaseStatus.Available)
        }

        if (purchase.date < LocalDate.now()) {
            return purchase.toProjection(status = PurchaseStatus.Overdue)
        }

        if (amount <= availableAmountToPurchaseDate) {
            val daysToPurchase = purchase.date.toEpochDay() - LocalDate.now().toEpochDay()
            val dailyLimit = (availableAmountToPurchaseDate - amount)
                .divide(daysToPurchase.toBigDecimal(), 10, RoundingMode.HALF_UP)
                .let {
                    exchangeRates.convert(
                        amount = it,
                        from = appConfig.dailyLimitCurrency,
                        to = displayCurrency,
                        date = LocalDate.now()
                    )
                }.setScale(2, RoundingMode.HALF_UP)
            return purchase.toProjection(
                status = PurchaseStatus.Planned(
                    dailyLimit,
                    displayCurrency
                )
            )
        }

        val gap = (amount - availableAmountToPurchaseDate)
            .let {
                exchangeRates.convert(
                    amount = it,
                    from = appConfig.dailyLimitCurrency,
                    to = displayCurrency,
                    date = LocalDate.now()
                )
            }.setScale(2, RoundingMode.HALF_UP)

        return purchase.toProjection(status = PurchaseStatus.Unreachable(gap, displayCurrency))
    }

}