package com.kholodkov.coinmonitor.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(idToken: String): Result<Unit>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun signOut()
}