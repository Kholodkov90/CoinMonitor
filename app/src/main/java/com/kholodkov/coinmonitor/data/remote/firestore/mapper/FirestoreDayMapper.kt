package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.model.day.Day
import com.kholodkov.coinmonitor.data.remote.firestore.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreDay
import com.kholodkov.coinmonitor.data.remote.firestore.parseFirestoreDate
import java.math.BigDecimal

fun FirestoreDay.toDay() = Day(
    date = date.parseFirestoreDate(),
    exchangeRate = exchangeRate?.let { BigDecimal(it) }
)

fun Day.toFirestore() = FirestoreDay(
    date = date.formatForFirestore(),
    exchangeRate = exchangeRate?.toString()
)