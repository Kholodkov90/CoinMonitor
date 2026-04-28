package com.kholodkov.coinmonitor.data.datasource.purchase

import com.kholodkov.coinmonitor.data.local.db.dao.PurchaseDao
import com.kholodkov.coinmonitor.data.local.db.mapper.toDomainList
import com.kholodkov.coinmonitor.data.local.db.mapper.toPlannedPurchases
import com.kholodkov.coinmonitor.data.local.db.mapper.toEntity
import com.kholodkov.coinmonitor.data.local.db.mapper.toRemote
import com.kholodkov.coinmonitor.data.model.purchase.BoughtPurchase
import com.kholodkov.coinmonitor.data.model.purchase.EditedPurchase
import com.kholodkov.coinmonitor.data.model.purchase.NewPurchase
import com.kholodkov.coinmonitor.data.model.purchase.ResolvedPurchase
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class PurchaseLocalDataSource @Inject constructor(
    private val purchaseDao: PurchaseDao
) {
    fun observeAll() = purchaseDao.observeAll().map { it.toDomainList() }

    fun observePlanned() = purchaseDao.observePlanned().map { it.toPlannedPurchases() }


    suspend fun getRemoteModel(uid: String) = purchaseDao.getSyncEntity(uid)?.toRemote()

    suspend fun insert(purchase: NewPurchase) {
        purchaseDao.upsert(purchase.toEntity())
    }

    suspend fun resolve(purchase: ResolvedPurchase) {
        val existing = purchaseDao.getByUid(purchase.uid)
        val resolvedEntity = purchase.toEntity()

        if (existing == null) {
            purchaseDao.upsert(resolvedEntity)
        } else if (existing.updatedAt < Instant.ofEpochMilli(purchase.updatedAt)) {
            purchaseDao.upsert(resolvedEntity.copy(id = existing.id))
        }
    }

    suspend fun edit(purchase: EditedPurchase) {
        val existing = purchaseDao.getByUid(purchase.uid)
            ?: error("No purchase with uid ${purchase.uid}")
        purchaseDao.upsert(
            existing.copy(
                amount = purchase.amount,
                currency = purchase.currency,
                dayId = purchase.dayId,
                description = purchase.description,
                updatedAt = Instant.now()
            )
        )
    }

    suspend fun buy(purchase: BoughtPurchase) {
        val existing = purchaseDao.getByUid(purchase.uid)
            ?: error("No purchase with uid ${purchase.uid}")
        purchaseDao.upsert(
            existing.copy(
                amount = purchase.amount,
                currency = purchase.currency,
                dayId = purchase.dayId,
                transactionId = purchase.transactionId,
                description = purchase.description,
                updatedAt = Instant.now()
            )
        )
    }


    suspend fun delete(uid: String) {
        purchaseDao.deleteByUid(uid)
    }
}