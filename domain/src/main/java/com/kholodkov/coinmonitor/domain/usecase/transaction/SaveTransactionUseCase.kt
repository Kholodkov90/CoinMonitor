package com.kholodkov.coinmonitor.domain.usecase.transaction

import com.kholodkov.coinmonitor.domain.model.transaction.SaveTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.toEditTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.toNewTransactionParams
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(params: SaveTransactionParams) {
        if (params.uid == null) {
            transactionRepository.addNew(params.toNewTransactionParams())
        } else {
            transactionRepository.edit(params.toEditTransactionParams())
        }
    }
}