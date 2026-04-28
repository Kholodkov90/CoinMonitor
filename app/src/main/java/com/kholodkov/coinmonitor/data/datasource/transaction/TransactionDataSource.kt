package com.kholodkov.coinmonitor.data.datasource.transaction

import com.kholodkov.coinmonitor.data.model.transaction.EditedTransaction
import com.kholodkov.coinmonitor.data.model.transaction.NewTransaction
import com.kholodkov.coinmonitor.data.model.transaction.ResolvedTransaction
import java.time.LocalDate
import javax.inject.Inject

class TransactionDataSource @Inject constructor(
    private val remoteDataSource: TransactionRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource
) {
    fun observeRemoteChanges() = remoteDataSource.observeChanges()

    fun observeByDate(date: LocalDate) = localDataSource.observeByDate(date)

    fun observeUpToDate(date: LocalDate) = localDataSource.observeUpToDate(date)

    fun observeAll() = localDataSource.observeAll()

    suspend fun getIdByUid(uid: String) = localDataSource.getIdByUid(uid)

    suspend fun addNew(transaction: NewTransaction): Long {
        val id = localDataSource.insert(transaction)
        val remoteModel = localDataSource.getRemoteModel(transaction.uid)
            ?: error("No transaction after insert ${transaction.uid}")
        remoteDataSource.push(remoteModel)
        return id
    }

    suspend fun edit(transaction: EditedTransaction) {
        localDataSource.edit(transaction)
        val remoteModel = localDataSource.getRemoteModel(transaction.uid)
            ?: error("No purchase after edit ${transaction.uid}")
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

    suspend fun resolve(purchase: ResolvedTransaction) {
        localDataSource.resolve(purchase)
    }
}