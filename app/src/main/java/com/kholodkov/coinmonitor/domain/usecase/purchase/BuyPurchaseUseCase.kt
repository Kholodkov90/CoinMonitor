package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.purchase.EditPurchaseParams
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import javax.inject.Inject

class BuyPurchaseUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(params: EditPurchaseParams) {
        purchaseRepository.buy(params)
    }
}