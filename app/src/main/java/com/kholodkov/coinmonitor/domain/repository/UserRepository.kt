package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.user.User

interface UserRepository {
    suspend fun ensureUserExists(user: User)
}