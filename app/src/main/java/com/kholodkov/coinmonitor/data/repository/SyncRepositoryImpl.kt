package com.kholodkov.coinmonitor.data.repository

import com.google.gson.Gson
import com.kholodkov.coinmonitor.data.datasource.day.DayDataSource
import com.kholodkov.coinmonitor.data.datasource.orphaned.OrphanedDataSource
import com.kholodkov.coinmonitor.data.datasource.purchase.PurchaseDataSource
import com.kholodkov.coinmonitor.data.datasource.transaction.TransactionDataSource
import com.kholodkov.coinmonitor.data.datasource.user.UserDataSource
import com.kholodkov.coinmonitor.data.mapper.toResolved
import com.kholodkov.coinmonitor.data.model.day.Day
import com.kholodkov.coinmonitor.data.model.orphaned.Orphaned
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchaseChange
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransactionChange
import com.kholodkov.coinmonitor.data.datasource.orphaned.OrphanedType
import com.kholodkov.coinmonitor.domain.model.User
import com.kholodkov.coinmonitor.domain.repository.SyncRepository
import com.kholodkov.coinmonitor.domain.scheduler.LoadExchangeRateScheduler
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val dayDataSource: DayDataSource,
    private val transactionDataSource: TransactionDataSource,
    private val purchaseDataSource: PurchaseDataSource,
    private val orphanedDataSource: OrphanedDataSource,
    private val loadExchangeRateScheduler: LoadExchangeRateScheduler,
    private val gson: Gson
) : SyncRepository {

    override suspend fun sync() {
        merge(
            userDataSource.observeRemoteChanges().map { SyncEvent.Users(it) },
            dayDataSource.observeRemoteChanges().map { SyncEvent.Days(it) },
            transactionDataSource.observeRemoteChanges().map { SyncEvent.Transactions(it) },
            purchaseDataSource.observeRemoteChanges().map { SyncEvent.Purchases(it) },
            dayDataSource.observeHasDaysWithoutRate()
                .filter { it }
                .map { SyncEvent.RatesMissing }
        ).collect { event ->
            when (event) {
                is SyncEvent.Users -> syncUsers(event.users)
                is SyncEvent.Days -> syncDays(event.days)
                is SyncEvent.Transactions -> syncTransactions(event.transactions)
                is SyncEvent.Purchases -> syncPurchases(event.purchases)
                is SyncEvent.RatesMissing -> syncRates()
            }
        }
    }

    private suspend fun syncUsers(users: List<User>) {
        users.forEach { user ->
            userDataSource.resolve(user)
        }
        resolveOrphaned()
    }

    private suspend fun resolveOrphaned() {
        val orphaned = orphanedDataSource.getAll()
        orphaned.forEach { orphan ->
            when (orphan.type) {
                OrphanedType.TRANSACTION -> {
                    val transaction = gson.fromJson(orphan.rawJson, RemoteTransaction::class.java)
                    handleRemoteTransaction(transaction).onSuccess {
                        orphanedDataSource.delete(orphan.id)
                    }
                }

                OrphanedType.PURCHASE -> {
                    val purchase = gson.fromJson(orphan.rawJson, RemotePurchase::class.java)
                    handleRemotePurchase(purchase).onSuccess {
                        orphanedDataSource.delete(orphan.id)
                    }
                }
            }
        }
    }

    private suspend fun syncDays(days: List<Day>) = days.forEach { day ->
        dayDataSource.resolve(day)
    }

    private suspend fun syncTransactions(changes: List<RemoteTransactionChange>) =
        changes.forEach { change ->
            when (change) {
                is RemoteTransactionChange.Delete -> transactionDataSource.applyRemoteDelete(change.uid)
                is RemoteTransactionChange.Upsert -> {
                    handleRemoteTransaction(change.transaction).onFailure {
                        addToOrphaned(
                            type = OrphanedType.TRANSACTION,
                            model = change.transaction
                        )
                    }
                }
            }
        }

    private suspend fun handleRemoteTransaction(transaction: RemoteTransaction) = runCatching {
        val userId = userDataSource.getIdByUid(transaction.userUid)
            ?: error("No user with uid ${transaction.userUid}")
        val dayId = dayDataSource.getOrCreateDayId(transaction.date)
        transactionDataSource.resolve(transaction.toResolved(dayId, userId))
    }

    private suspend fun syncPurchases(changes: List<RemotePurchaseChange>) =
        changes.forEach { change ->
            when (change) {
                is RemotePurchaseChange.Delete -> purchaseDataSource.applyRemoteDelete(change.uid)
                is RemotePurchaseChange.Upsert -> {
                    handleRemotePurchase(change.purchase).onFailure {
                        addToOrphaned(
                            type = OrphanedType.PURCHASE,
                            model = change.purchase
                        )
                    }
                }
            }
        }

    private suspend fun handleRemotePurchase(purchase: RemotePurchase) = runCatching {
        val userId = userDataSource.getIdByUid(purchase.userUid)
            ?: error("No user with uid ${purchase.userUid}")
        val dayId = dayDataSource.getOrCreateDayId(purchase.date)
        purchaseDataSource.resolve(purchase.toResolved(dayId, userId))
    }

    private suspend fun <T> addToOrphaned(type: OrphanedType, model: T) {
        orphanedDataSource.insert(
            Orphaned(
                type = type,
                rawJson = gson.toJson(model)
            )
        )
    }

    private fun syncRates() = loadExchangeRateScheduler.loadRates()

    private sealed class SyncEvent {
        data class Users(val users: List<User>) : SyncEvent()
        data class Days(val days: List<Day>) : SyncEvent()
        data class Transactions(val transactions: List<RemoteTransactionChange>) : SyncEvent()
        data class Purchases(val purchases: List<RemotePurchaseChange>) : SyncEvent()
        object RatesMissing : SyncEvent()
    }
}