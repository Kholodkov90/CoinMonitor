package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.user.UserEntity
import com.kholodkov.coinmonitor.domain.model.user.User


fun User.toUserEntity(id: Long) = UserEntity(
    id = id,
    uid = uid,
    name = displayName
)