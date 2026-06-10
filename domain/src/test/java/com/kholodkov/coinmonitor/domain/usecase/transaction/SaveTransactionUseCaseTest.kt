package com.kholodkov.coinmonitor.domain.usecase.transaction

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.SaveTransactionParams
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

class SaveTransactionUseCaseTest {
    private val transactionRepository = mockk<TransactionRepository>(relaxed = true)
    private val useCase = SaveTransactionUseCase(transactionRepository)

    private val params = SaveTransactionParams(
        uid = null,
        date = LocalDate.of(2026, 1, 1),
        amount = BigDecimal("100"),
        currency = Currency.RSD,
        time = LocalTime.NOON
    )

    @Test
    fun `when uid is null calls addNew`() = runTest {
        useCase(params.copy(uid = null))

        coVerify(exactly = 0) { transactionRepository.edit(any()) }
        coVerify(exactly = 1) { transactionRepository.addNew(any()) }
    }

    @Test
    fun `when uid is not null calls edit`() = runTest {
        useCase(params.copy(uid = "uid"))

        coVerify(exactly = 0) { transactionRepository.addNew(any()) }
        coVerify(exactly = 1) { transactionRepository.edit(any()) }
    }
}