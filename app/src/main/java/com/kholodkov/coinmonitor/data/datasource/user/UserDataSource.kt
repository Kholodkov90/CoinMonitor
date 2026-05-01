package com.kholodkov.coinmonitor.data.datasource.user

import com.kholodkov.coinmonitor.domain.model.user.User
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) {
    fun observeRemoteChanges() = remoteDataSource.observeChanges()

    fun observeDisplayName(uid: String) = localDataSource.observeDisplayName(uid)

    suspend fun ensureUserExists(user: User) = remoteDataSource.ensureUserExists(user)

    suspend fun getIdByUid(uid: String) = localDataSource.getIdByUid(uid)

    suspend fun resolve(user: User) = localDataSource.resolve(user)

    suspend fun updateUserName(user: User) {
        localDataSource.updateName(user)
        remoteDataSource.update(user)
    }
}