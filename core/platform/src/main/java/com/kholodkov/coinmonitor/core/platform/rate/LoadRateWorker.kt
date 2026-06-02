package com.kholodkov.coinmonitor.core.platform.rate

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kholodkov.coinmonitor.domain.usecase.currency.UpdateExchangeRatesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.fold

@HiltWorker
class LoadRateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val updateExchangeRatesUseCase: UpdateExchangeRatesUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        updateExchangeRatesUseCase().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )
}