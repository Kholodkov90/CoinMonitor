package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.purchase.Purchase
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.domain.model.purchase.toProjection
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
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<List<PurchaseProjection>> = combine(
        purchaseRepository.observeAll(),
        transactionRepository.observeAll(),
        exchangeRepository.observeExchangeRates(),
        settingsRepository.observeDisplayCurrency()
    ) { purchases, transactions, exchangeRates, currency ->
        val spentByDate = transactions.calculateSpentByDate(
            exchangeRates = exchangeRates,
            currency = currency
        )

        purchases.fold(emptyList()) { handledPurchases, purchase ->
            val purchaseDate =
                if (purchase.date < LocalDate.now()) LocalDate.now() else purchase.date
            val availableAmountToPurchaseDate = calculateAvailableAmount(
                date = purchaseDate,
                currency = currency,
                exchangeRates = exchangeRates,
                spentByDate = spentByDate,
                handledPurchases = handledPurchases
            )

            val availableAmountNow = if (purchaseDate == LocalDate.now()) {
                availableAmountToPurchaseDate
            } else {
                calculateAvailableAmount(
                    date = LocalDate.now(),
                    currency = currency,
                    exchangeRates = exchangeRates,
                    spentByDate = spentByDate,
                    handledPurchases = handledPurchases
                )
            }

            handledPurchases + buildProjection(
                purchase = purchase,
                availableAmountToPurchaseDate = availableAmountToPurchaseDate,
                availableAmountNow = availableAmountNow,
                currency = currency,
                exchangeRates = exchangeRates,
            )
        }
    }


    private fun calculateAvailableAmount(
        date: LocalDate,
        currency: Currency,
        exchangeRates: ExchangeRates,
        spentByDate: Map<LocalDate, BigDecimal>,
        handledPurchases: List<PurchaseProjection>
    ): BigDecimal {
        val spentBefore = spentByDate.filter { it.key <= date }
            .values
            .sumOf { it }

        val plannedBefore = handledPurchases
            .filter { it.status !is PurchaseStatus.Completed }
            .sumOf {
                exchangeRates.convert(
                    amount = it.amount,
                    from = it.currency,
                    to = currency,
                    date = it.date
                )
            }

        val totalSpent = spentBefore.plus(plannedBefore)

        return calculateBudget(
            date = date,
            exchangeRates = exchangeRates,
            currency = currency,
            totalSpent = totalSpent
        )
    }

    private fun buildProjection(
        purchase: Purchase,
        availableAmountToPurchaseDate: BigDecimal,
        availableAmountNow: BigDecimal,
        currency: Currency,
        exchangeRates: ExchangeRates,
    ): PurchaseProjection {
        if (purchase.transactionUid != null) {
            return purchase.toProjection(status = PurchaseStatus.Completed)
        }

        val amountInCurrency = exchangeRates.convert(
            amount = purchase.amount,
            from = purchase.currency,
            to = currency,
            date = purchase.date
        )

        if (amountInCurrency <= availableAmountNow) {
            return purchase.toProjection(status = PurchaseStatus.Available)
        }

        if (purchase.date < LocalDate.now()) {
            return purchase.toProjection(status = PurchaseStatus.Overdue)
        }

        if (amountInCurrency <= availableAmountToPurchaseDate) {
            val daysToPurchase = purchase.date.toEpochDay() - LocalDate.now().toEpochDay()
            val dailyLimit = (availableAmountToPurchaseDate - amountInCurrency)
                .divide(daysToPurchase.toBigDecimal(), 2, RoundingMode.HALF_UP)
            return purchase.toProjection(status = PurchaseStatus.Pending(dailyLimit, currency))
        }

        val gap = amountInCurrency - availableAmountToPurchaseDate
        return purchase.toProjection(status = PurchaseStatus.Unreachable(gap, currency))
    }

}