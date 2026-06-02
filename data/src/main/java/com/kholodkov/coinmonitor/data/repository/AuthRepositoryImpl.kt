package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.auth.AuthDataSource
import com.kholodkov.coinmonitor.data.datasource.user.UserDataSource
import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import com.kholodkov.coinmonitor.domain.scheduler.SyncUserScheduler
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val syncUserScheduler: SyncUserScheduler
) : AuthRepository {

    override suspend fun signIn(idToken: String) = runCatching {
        val user = authDataSource.signIn(idToken).getOrThrow()
        userDataSource.resolve(user)
        syncUserScheduler.syncUser(user)
    }

    override fun observeIsLoggedIn() = authDataSource.observeIsLoggedIn()

    override fun signOut() = authDataSource.signOut()
}