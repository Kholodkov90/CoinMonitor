package com.kholodkov.coinmonitor.data.datasource.user

import com.kholodkov.coinmonitor.data.local.db.dao.UserDao
import com.kholodkov.coinmonitor.data.local.db.entity.user.UserEntity
import com.kholodkov.coinmonitor.domain.model.user.User
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getIdByUid(uid: String) = userDao.getIdByUid(uid)

    suspend fun resolve(user: User) {
        val existingId = userDao.getIdByUid(user.uid)
        userDao.upsert(
            UserEntity(
                id = existingId ?: 0,
                uid = user.uid,
                name = user.displayName
            )
        )
    }
}