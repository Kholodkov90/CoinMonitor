package com.kholodkov.coinmonitor.domain.scheduler

import com.kholodkov.coinmonitor.domain.model.user.User

interface SyncUserScheduler {
    fun syncUser(user: User)
}