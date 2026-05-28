package com.kholodkov.coinmonitor.data.remote.firestore.model

import androidx.annotation.Keep

@Keep
data class FirestoreTransaction(
    val userUid: String = "",
    val amount: String = "",
    val currency: String = "",
    val time: String = "",
    val updatedAt: Long = 0L
)