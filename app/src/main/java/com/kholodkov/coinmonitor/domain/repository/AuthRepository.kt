package com.kholodkov.coinmonitor.domain.repository

import androidx.credentials.Credential
import com.kholodkov.coinmonitor.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(credential: Credential): Result<Unit>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun signOut()
}