package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseSummary
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

class ObservePurchaseSummaryUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val exchangeRepository: ExchangeRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<PurchaseSummary> = combine(
        purchaseRepository.observePlanned(),
        exchangeRepository.observeExchangeRates(),
        settingsRepository.observeDisplayCurrency()
    ) { purchases, exchangeRates, currency ->

        val totalAmount = purchases.fold(BigDecimal.ZERO) { totalAmount, purchase ->
            totalAmount.plus(
                exchangeRates.convert(
                    amount = purchase.amount,
                    from = purchase.currency,
                    to = currency,
                    date = purchase.date
                )
            )
        }

        PurchaseSummary(
            totalAmount = totalAmount,
            currency = currency
        )
    }
}