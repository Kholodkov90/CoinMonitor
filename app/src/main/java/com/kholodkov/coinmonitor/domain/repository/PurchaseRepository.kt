package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.data.model.purchase.PlannedPurchase
import com.kholodkov.coinmonitor.domain.model.purchase.EditPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.NewPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.Purchase
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    fun observeAll(): Flow<List<Purchase>>
    fun observePlanned(): Flow<List<PlannedPurchase>>
    suspend fun addNew(params: NewPurchaseParams)
    suspend fun edit(params: EditPurchaseParams)
    suspend fun restore(params: RestorePurchaseParams)
    suspend fun buy(params: EditPurchaseParams)
    suspend fun delete(uid: String)
}