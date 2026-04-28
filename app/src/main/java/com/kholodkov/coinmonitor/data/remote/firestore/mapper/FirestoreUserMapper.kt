package com.kholodkov.coinmonitor.data.remote.firestore.mapper

import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreUser
import com.kholodkov.coinmonitor.domain.model.user.User

fun FirestoreUser.toUser(uid: String) = User(
    uid = uid,
    displayName = displayName
)

fun User.toFirestore() = FirestoreUser(
    displayName = displayName
)