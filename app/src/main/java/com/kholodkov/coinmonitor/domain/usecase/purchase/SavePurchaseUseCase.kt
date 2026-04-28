package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.purchase.SavePurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.toEditPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.toNewPurchaseParams
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import javax.inject.Inject

class SavePurchaseUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(params: SavePurchaseParams) {
        if (params.uid == null) {
            purchaseRepository.addNew(params.toNewPurchaseParams())
        } else {
            purchaseRepository.edit(params.toEditPurchaseParams())
        }
    }
}