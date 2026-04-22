package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreUser
import com.kholodkov.coinmonitor.domain.model.User

fun FirestoreUser.toUser() = User(
    uid = uid,
    displayName = displayName
)

fun User.toFirestore() = FirestoreUser(
    uid = uid,
    displayName = displayName
)