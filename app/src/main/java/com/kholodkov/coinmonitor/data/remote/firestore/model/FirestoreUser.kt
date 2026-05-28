package com.kholodkov.coinmonitor.data.remote.firestore.model

import androidx.annotation.Keep

@Keep
data class FirestoreUser(
    val displayName: String = ""
)