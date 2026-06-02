package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeDisplayName(): Flow<String>
    suspend fun ensureUserExists(user: User)
    suspend fun setDisplayName(name: String)
}