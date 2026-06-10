package com.kholodkov.coinmonitor.domain.usecase.purchase

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.SavePurchaseParams
import com.kholodkov.coinmonitor.domain.repository.PurchaseRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class SavePurchaseUseCaseTest {

    private val purchaseRepository = mockk<PurchaseRepository>(relaxed = true)

    private val useCase = SavePurchaseUseCase(purchaseRepository)

    private val params = SavePurchaseParams(
        uid = null,
        date = LocalDate.of(2026, 1, 1),
        amount = BigDecimal("100"),
        currency = Currency.RSD,
        description = "description"
    )

    @Test
    fun `when uid is null calls addNew`() = runTest {
        useCase(params.copy(uid = null))

        coVerify(exactly = 0) { purchaseRepository.edit(any()) }
        coVerify(exactly = 1) { purchaseRepository.addNew(any()) }
    }

    @Test
    fun `when uid is not null calls edit`() = runTest {
        useCase(params.copy(uid = "uid"))

        coVerify(exactly = 0) { purchaseRepository.addNew(any()) }
        coVerify(exactly = 1) { purchaseRepository.edit(any()) }
    }
}