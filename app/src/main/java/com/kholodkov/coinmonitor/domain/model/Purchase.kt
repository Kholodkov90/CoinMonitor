package com.kholodkov.coinmonitor.domain.model

import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class Purchase(
    val uid: String,
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: Currency,
    val description: String,
    val userName: String
)
