package com.kholodkov.coinmonitor.data.remote.firestore.model

data class FirestorePurchase(
    val uid: String = "",
    val date: String = "",
    val userUid: String = "",
    val amount: String = "",
    val currency: String = "",
    val description: String = "",
    val updatedAt: Long = 0L
)