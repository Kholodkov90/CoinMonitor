package com.kholodkov.coinmonitor.infrastructure.rate

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kholodkov.coinmonitor.domain.scheduler.LoadExchangeRateScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoadExchangeRateManager @Inject constructor(
    private val workManager: WorkManager
) : LoadExchangeRateScheduler {
    override fun loadRates() {
        val request = OneTimeWorkRequestBuilder<LoadRateWorker>()
            .setConstraints(
                Constraints(requiredNetworkType = NetworkType.CONNECTED)
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "fetch_rates",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

}