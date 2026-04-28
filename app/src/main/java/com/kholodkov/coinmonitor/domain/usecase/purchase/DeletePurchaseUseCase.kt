package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import javax.inject.Inject

class DeletePurchaseUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(uid: String) = purchaseRepository.delete(uid)
}