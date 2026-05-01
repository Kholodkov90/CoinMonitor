package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.auth.AuthDataSource
import com.kholodkov.coinmonitor.data.datasource.user.UserDataSource
import com.kholodkov.coinmonitor.domain.model.user.User
import com.kholodkov.coinmonitor.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource
) : UserRepository {
    override fun observeDisplayName() =
        userDataSource.observeDisplayName(authDataSource.getCurrentUser().uid)

    override suspend fun ensureUserExists(user: User) = userDataSource.ensureUserExists(user)
    override suspend fun setDisplayName(name: String) {
        userDataSource.updateUserName(
            authDataSource.getCurrentUser().copy(displayName = name)
        )

    }
}