package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.purchase.Purchase
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.domain.model.purchase.toProjection
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import com.kholodkov.coinmonitor.domain.tools.calculateBudget
import com.kholodkov.coinmonitor.domain.tools.calculateSpentByDate
import com.kholodkov.coinmonitor.domain.tools.convertTo
import com.kholodkov.coinmonitor.domain.tools.rateFor
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
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): Flow<List<PurchaseProjection>> = combine(
        purchaseRepository.observeAll(),
        transactionRepository.observeAll(),
        exchangeRepository.observeAll(),
        preferencesRepository.observeDisplayCurrency()
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
                rate = exchangeRates.rateFor(purchase.date),
            )
        }
    }


    private fun calculateAvailableAmount(
        date: LocalDate,
        currency: Currency,
        exchangeRates: List<ExchangeRate>,
        spentByDate: Map<LocalDate, BigDecimal>,
        handledPurchases: List<PurchaseProjection>
    ): BigDecimal {
        val spentBefore = spentByDate.filter { it.key <= date }
            .values
            .sumOf { it }

        val plannedBefore = handledPurchases
            .filter { it.status !is PurchaseStatus.Completed }
            .sumOf {
                it.amount.convertTo(
                    from = it.currency,
                    to = currency,
                    rate = exchangeRates.rateFor(it.date)
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
        rate: BigDecimal,
    ): PurchaseProjection {
        if (purchase.transactionUid != null) {
            return purchase.toProjection(status = PurchaseStatus.Completed)
        }

        val amountInCurrency = purchase.amount.convertTo(purchase.currency, currency, rate)

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