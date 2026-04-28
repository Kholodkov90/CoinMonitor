package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.auth.AuthDataSource
import com.kholodkov.coinmonitor.data.datasource.day.DayDataSource
import com.kholodkov.coinmonitor.data.datasource.transaction.TransactionDataSource
import com.kholodkov.coinmonitor.data.datasource.user.UserDataSource
import com.kholodkov.coinmonitor.data.local.tools.UidGenerator
import com.kholodkov.coinmonitor.data.mapper.toEditedTransaction
import com.kholodkov.coinmonitor.data.mapper.toNewTransaction
import com.kholodkov.coinmonitor.domain.model.transaction.EditTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.NewTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import java.time.LocalDate
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dayDataSource: DayDataSource,
    private val transactionDataSource: TransactionDataSource,
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource,
    private val uidGenerator: UidGenerator,
) : TransactionRepository {
    override fun observeByDate(date: LocalDate) =
        transactionDataSource.observeByDate(date)

    override fun observeUpToDate(date: LocalDate) = transactionDataSource.observeUpToDate(date)

    override fun observeAll() = transactionDataSource.observeAll()

    override suspend fun addNew(params: NewTransactionParams) {
        val currentUser = authDataSource.getCurrentUser() ?: error("User is not logged in")
        val userId = userDataSource.getIdByUid(currentUser.uid)
            ?: error("User ${currentUser.uid} doesn't exists")
        transactionDataSource.addNew(
            params.toNewTransaction(
                uid = uidGenerator.generate(),
                dayId = dayDataSource.getOrCreateDayId(params.date),
                userId = userId,
            )
        )
    }

    override suspend fun edit(params: EditTransactionParams) =
        transactionDataSource.edit(params.toEditedTransaction())


    override suspend fun restore(params: RestoreTransactionParams) {
        val dayId = dayDataSource.getOrCreateDayId(params.date)
        val userId = userDataSource.getIdByUid(params.userUid)
            ?: error("User ${params.uid} not found")
        transactionDataSource.addNew(
            params.toNewTransaction(
                dayId = dayId,
                userId = userId
            )
        )
    }

    override suspend fun delete(uid: String) {
        transactionDataSource.delete(uid)
    }
}