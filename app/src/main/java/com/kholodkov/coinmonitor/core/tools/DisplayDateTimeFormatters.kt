package com.kholodkov.coinmonitor.core.tools

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun LocalDate.toDisplayString(): String = format(dateFormatter)
fun LocalTime.toDisplayString(): String = format(timeFormatter)

fun String.fromDisplayDate(locale: Locale = Locale.getDefault()): LocalDate =
    LocalDate.parse(this, dateFormatter)

fun String.fromDisplayTime(locale: Locale = Locale.getDefault()): LocalTime =
    LocalTime.parse(this, timeFormatter)
