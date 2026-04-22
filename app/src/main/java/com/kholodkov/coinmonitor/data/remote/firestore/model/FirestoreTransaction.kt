package com.kholodkov.coinmonitor.data.remote.firestore.model

data class FirestoreTransaction(
    val uid: String = "",
    val date: String = "",
    val userUid: String = "",
    val amount: String = "",
    val currency: String = "",
    val time: String = "",
    val updatedAt: Long = 0L
)