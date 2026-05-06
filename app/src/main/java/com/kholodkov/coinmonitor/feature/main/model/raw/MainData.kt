package com.kholodkov.coinmonitor.feature.main.model.raw

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import java.math.BigDecimal
import java.time.LocalDate

data class MainData(
    val date: LocalDate = LocalDate.now(),
    val balance: BigDecimal = BigDecimal.ZERO,
    val spent: BigDecimal = BigDecimal.ZERO,
    val remaining: BigDecimal = BigDecimal.ZERO,
    val currency: Currency = Currency.RSD,
    val transactions: List<Transaction> = listOf()
)