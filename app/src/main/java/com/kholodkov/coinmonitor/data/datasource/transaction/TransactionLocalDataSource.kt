package com.kholodkov.coinmonitor.data.datasource.transaction

import com.kholodkov.coinmonitor.data.local.db.dao.TransactionDao
import com.kholodkov.coinmonitor.data.local.db.mapper.toEntity
import com.kholodkov.coinmonitor.data.local.db.mapper.toRemote
import com.kholodkov.coinmonitor.data.model.transaction.EditedTransaction
import com.kholodkov.coinmonitor.data.model.transaction.NewTransaction
import com.kholodkov.coinmonitor.data.model.transaction.ResolvedTransaction
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class TransactionLocalDataSource @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun observeByDate(date: LocalDate) = transactionDao.observeTransactionsByDate(date)

    fun observeSpendsBefore(date: LocalDate) = transactionDao.observeSpendsBefore(date)

    fun observeSpendsByDate(date: LocalDate) = transactionDao.observeSpendsByDate(date)

    suspend fun getRemoteModel(uid: String) = transactionDao.getSyncEntity(uid)?.toRemote()

    suspend fun delete(uid: String) = transactionDao.deleteByUid(uid)

    suspend fun insert(transaction: NewTransaction) = transactionDao.insert(transaction.toEntity())

    suspend fun resolve(transaction: ResolvedTransaction) {
        val existing = transactionDao.getByUid(transaction.uid)
        val resolvedEntity = transaction.toEntity()

        if (existing == null) {
            transactionDao.upsert(resolvedEntity)
        } else if (existing.updatedAt < Instant.ofEpochMilli(transaction.updatedAt)) {
            transactionDao.upsert(resolvedEntity.copy(id = existing.id))
        }
    }

    suspend fun edit(transaction: EditedTransaction) {
        val existing = transactionDao.getByUid(transaction.uid)
            ?: error("No transaction with uid ${transaction.uid}")
        transactionDao.upsert(
            existing.copy(
                amount = transaction.amount,
                currency = transaction.currency,
                time = transaction.time,
                updatedAt = Instant.now()
            )
        )
    }
}