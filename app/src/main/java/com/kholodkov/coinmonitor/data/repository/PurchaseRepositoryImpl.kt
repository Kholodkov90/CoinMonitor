package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.auth.AuthDataSource
import com.kholodkov.coinmonitor.data.datasource.day.DayDataSource
import com.kholodkov.coinmonitor.data.datasource.purchase.PurchaseDataSource
import com.kholodkov.coinmonitor.data.datasource.transaction.TransactionDataSource
import com.kholodkov.coinmonitor.data.datasource.user.UserDataSource
import com.kholodkov.coinmonitor.data.local.tools.UidGenerator
import com.kholodkov.coinmonitor.data.mapper.toBoughtPurchase
import com.kholodkov.coinmonitor.data.mapper.toEditedPurchase
import com.kholodkov.coinmonitor.data.mapper.toNewPurchase
import com.kholodkov.coinmonitor.data.mapper.toNewTransaction
import com.kholodkov.coinmonitor.domain.model.purchase.EditPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.NewPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val purchaseDataSource: PurchaseDataSource,
    private val dayDataSource: DayDataSource,
    private val transactionDataSource: TransactionDataSource,
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource,
    private val uidGenerator: UidGenerator,
) : PurchaseRepository {
    override fun observeAll() = purchaseDataSource.observeAll()

    override fun observePlanned() = purchaseDataSource.observePlanned()

    override suspend fun addNew(params: NewPurchaseParams) {
        val currentUser = authDataSource.getCurrentUser() ?: error("User is not logged in")
        val userId = userDataSource.getIdByUid(currentUser.uid)
            ?: error("User ${currentUser.uid} doesn't exists")

        purchaseDataSource.addNew(
            params.toNewPurchase(
                uid = uidGenerator.generate(),
                dayId = dayDataSource.getOrCreateDayId(params.date),
                userId = userId,
            )
        )
    }

    override suspend fun edit(params: EditPurchaseParams) {
        purchaseDataSource.edit(
            params.toEditedPurchase(
                dayId = dayDataSource.getOrCreateDayId(params.date),
            )
        )
    }

    override suspend fun restore(params: RestorePurchaseParams) {
        val dayId = dayDataSource.getOrCreateDayId(params.date)
        val userId = userDataSource.getIdByUid(params.userUid)
            ?: error("User ${params.uid} not found")
        val transactionId = params.transactionUid?.let {
            transactionDataSource.getIdByUid(params.transactionUid)
        }
        purchaseDataSource.addNew(
            params.toNewPurchase(
                dayId = dayId,
                userId = userId,
                transactionId = transactionId
            )
        )
    }

    override suspend fun buy(params: EditPurchaseParams) {
        val currentUser = authDataSource.getCurrentUser() ?: error("User is not logged in")
        val userId = userDataSource.getIdByUid(currentUser.uid)
            ?: error("User ${currentUser.uid} doesn't exists")
        val dayId = dayDataSource.getOrCreateDayId(params.date)
        val transactionId = transactionDataSource.addNew(
            params.toNewTransaction(
                uid = uidGenerator.generate(),
                dayId = dayId,
                userId = userId,
            )
        )

        purchaseDataSource.buy(
            params.toBoughtPurchase(
                dayId = dayId,
                transactionId = transactionId
            )
        )
    }

    override suspend fun delete(uid: String) {
        purchaseDataSource.delete(uid)
    }
}