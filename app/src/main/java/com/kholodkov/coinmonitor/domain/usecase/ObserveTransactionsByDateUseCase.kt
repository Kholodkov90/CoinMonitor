package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.domain.model.Transaction
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class ObserveTransactionsByDateUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Transaction>> {
        return transactionRepository.observeByDate(date)
    }
}