package com.kholodkov.coinmonitor.data.remote.firestore.model

data class FirestoreDay(
    val date: String = "",
    val exchangeRate: String? = null
)