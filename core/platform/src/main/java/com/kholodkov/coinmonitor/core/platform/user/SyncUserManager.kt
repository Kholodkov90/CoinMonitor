package com.kholodkov.coinmonitor.core.platform.user

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kholodkov.coinmonitor.domain.model.user.User
import com.kholodkov.coinmonitor.domain.scheduler.SyncUserScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncUserManager @Inject constructor(
    private val workManager: WorkManager
) : SyncUserScheduler {
    override fun syncUser(user: User) {
        val request = OneTimeWorkRequestBuilder<SyncUserWorker>()
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setInputData(
                workDataOf(
                    SyncUserWorker.KEY_UID to user.uid,
                    SyncUserWorker.KEY_DISPLAY_NAME to user.displayName
                )
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "initial_sync_${user.uid}",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}