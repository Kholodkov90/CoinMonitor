package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import javax.inject.Inject

class RestorePurchaseUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(params: RestorePurchaseParams) =
        purchaseRepository.restore(params)
}