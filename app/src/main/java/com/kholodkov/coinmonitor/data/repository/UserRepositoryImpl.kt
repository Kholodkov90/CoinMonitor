package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.user.UserDataSource
import com.kholodkov.coinmonitor.domain.model.user.User
import com.kholodkov.coinmonitor.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override suspend fun ensureUserExists(user: User) = userDataSource.ensureUserExists(user)
}