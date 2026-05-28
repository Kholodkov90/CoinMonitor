package com.kholodkov.coinmonitor.data.remote.firestore.model

import androidx.annotation.Keep

@Keep
data class FirestoreCurrency(
    val exchangeRate: Double = 0.0
)