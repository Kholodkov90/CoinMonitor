package com.kholodkov.coinmonitor.data.model.day

import java.math.BigDecimal
import java.time.LocalDate

data class Day(
    val date: LocalDate,
    val exchangeRate: BigDecimal?
)
