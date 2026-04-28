package com.kholodkov.coinmonitor.data.remote.firestore.model

data class FirestorePurchase(
    val userUid: String = "",
    val date: String = "",
    val amount: String = "",
    val transactionUid: String? = null,
    val currency: String = "",
    val description: String = "",
    val updatedAt: Long = 0L
)