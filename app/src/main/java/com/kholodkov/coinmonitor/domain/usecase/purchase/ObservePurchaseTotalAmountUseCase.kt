package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

class ObservePurchaseTotalAmountUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val exchangeRepository: ExchangeRepository,
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<BigDecimal> = combine(
        purchaseRepository.observePlanned(),
        exchangeRepository.observeExchangeRates(),
        preferencesRepository.observeDisplayCurrency()
    ) { purchases, exchangeRates, currency ->

        purchases.fold(BigDecimal.ZERO) { totalAmount, purchase ->
            totalAmount.plus(
                exchangeRates.convert(
                    amount = purchase.amount,
                    from = purchase.currency,
                    to = currency,
                    date = purchase.date
                )
            )
        }
    }
}