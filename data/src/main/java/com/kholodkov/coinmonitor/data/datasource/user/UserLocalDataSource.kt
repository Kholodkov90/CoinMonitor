package com.kholodkov.coinmonitor.data.datasource.user

import com.kholodkov.coinmonitor.data.local.db.dao.UserDao
import com.kholodkov.coinmonitor.data.local.db.mapper.toUserEntity
import com.kholodkov.coinmonitor.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao
) {

    fun observeDisplayName(uid: String): Flow<String> = userDao.observeDisplayName(uid).map {
        it ?: error("User with uid $uid not found")
    }

    suspend fun getIdByUid(uid: String) = userDao.getIdByUid(uid)

    suspend fun resolve(user: User) {
        val existingId = userDao.getIdByUid(user.uid) ?: 0
        userDao.upsert(user.toUserEntity(existingId))
    }

    suspend fun updateName(user: User) {
        val existingId = userDao.getIdByUid(user.uid)
            ?: error("User with uid ${user.uid} not found")
        userDao.upsert(user.toUserEntity(existingId))
    }

}