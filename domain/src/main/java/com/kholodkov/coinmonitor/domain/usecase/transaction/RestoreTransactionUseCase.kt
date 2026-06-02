package com.kholodkov.coinmonitor.domain.usecase.transaction

import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import javax.inject.Inject

class RestoreTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(params: RestoreTransactionParams) =
        transactionRepository.restore(params)
}