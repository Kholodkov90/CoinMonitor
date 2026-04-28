package com.kholodkov.coinmonitor.data.remote.firebaseAuth.mapper

import com.google.firebase.auth.FirebaseUser
import com.kholodkov.coinmonitor.domain.model.user.User

fun FirebaseUser.toUser() = User(
    uid = uid,
    displayName = displayName ?: ""
)