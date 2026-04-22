package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.CurrencySum
import com.kholodkov.coinmonitor.domain.model.EditTransactionParams
import com.kholodkov.coinmonitor.domain.model.NewTransactionParams
import com.kholodkov.coinmonitor.domain.model.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun observeByDate(date: LocalDate): Flow<List<Transaction>>
    fun observeSpendsBefore(date: LocalDate): Flow<List<CurrencySum>>
    fun observeSpendsByDate(date: LocalDate): Flow<List<CurrencySum>>
    suspend fun addNew(params: NewTransactionParams)
    suspend fun edit(params: EditTransactionParams)
    suspend fun restore(params: RestoreTransactionParams)
    suspend fun delete(uid: String)
}