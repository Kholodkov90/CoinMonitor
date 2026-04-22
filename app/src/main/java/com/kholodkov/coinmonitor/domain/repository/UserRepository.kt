package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.User

interface UserRepository {
    suspend fun ensureUserExists(user: User)
}