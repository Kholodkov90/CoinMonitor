package com.kholodkov.coinmonitor.data.remote.firestore.tools

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val FIRESTORE_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val FIRESTORE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

fun LocalDate.formatForFirestore(): String = format(FIRESTORE_DATE_FORMAT)
fun String.parseFirestoreDate(): LocalDate = LocalDate.parse(this, FIRESTORE_DATE_FORMAT)

fun LocalTime.formatForFirestore(): String = format(FIRESTORE_TIME_FORMAT)
fun String.parseFirestoreTime(): LocalTime = LocalTime.parse(this, FIRESTORE_TIME_FORMAT)