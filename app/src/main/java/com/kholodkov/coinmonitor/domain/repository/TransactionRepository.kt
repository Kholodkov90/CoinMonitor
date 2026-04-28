package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.transaction.EditTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.NewTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun observeByDate(date: LocalDate): Flow<List<Transaction>>
    fun observeUpToDate(date: LocalDate): Flow<List<Transaction>>
    fun observeAll(): Flow<List<Transaction>>
    suspend fun addNew(params: NewTransactionParams)
    suspend fun edit(params: EditTransactionParams)
    suspend fun restore(params: RestoreTransactionParams)
    suspend fun delete(uid: String)
}