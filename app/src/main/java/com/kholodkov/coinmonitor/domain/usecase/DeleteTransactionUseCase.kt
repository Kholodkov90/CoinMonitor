package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(uid: String) {
        transactionRepository.delete(uid)
    }
}