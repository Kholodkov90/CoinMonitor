package com.kholodkov.coinmonitor.data.datasource.purchase

import com.kholodkov.coinmonitor.data.model.purchase.BoughtPurchase
import com.kholodkov.coinmonitor.data.model.purchase.EditedPurchase
import com.kholodkov.coinmonitor.data.model.purchase.NewPurchase
import com.kholodkov.coinmonitor.data.model.purchase.ResolvedPurchase
import javax.inject.Inject

class PurchaseDataSource @Inject constructor(
    private val localDataSource: PurchaseLocalDataSource,
    private val remoteDataSource: PurchaseRemoteDataSource
) {
    fun observeRemoteChanges() = remoteDataSource.observeChanges()

    fun observeAll() = localDataSource.observeAll()

    fun observePlanned() = localDataSource.observePlanned()


    suspend fun addNew(purchase: NewPurchase) {
        localDataSource.insert(purchase)
        val remoteModel = localDataSource.getRemoteModel(purchase.uid)
            ?: error("No purchase after insert ${purchase.uid}")

        remoteDataSource.push(remoteModel)
    }


    suspend fun edit(purchase: EditedPurchase) {
        localDataSource.edit(purchase)
        val remoteModel = localDataSource.getRemoteModel(purchase.uid)
            ?: error("No purchase after edit ${purchase.uid}")
        remoteDataSource.push(remoteModel)
    }

    suspend fun buy(purchase: BoughtPurchase) {
        localDataSource.buy(purchase)
        val remoteModel = localDataSource.getRemoteModel(purchase.uid)
            ?: error("No purchase after edit ${purchase.uid}")
        remoteDataSource.push(remoteModel)
    }

    suspend fun delete(uid: String) {
        val remoteModel = localDataSource.getRemoteModel(uid) ?: return
        localDataSource.delete(uid)
        remoteDataSource.delete(remoteModel)
    }

    suspend fun applyRemoteDelete(uid: String) {
        localDataSource.delete(uid)
    }

    suspend fun resolve(purchase: ResolvedPurchase) {
        localDataSource.resolve(purchase)
    }
}