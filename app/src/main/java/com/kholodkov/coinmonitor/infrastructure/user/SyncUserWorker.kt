package com.kholodkov.coinmonitor.infrastructure.user

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kholodkov.coinmonitor.domain.model.User
import com.kholodkov.coinmonitor.domain.usecase.EnsureUserExistsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncUserWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val ensureUserExistsUseCase: EnsureUserExistsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = inputData.getString(KEY_UID) ?: return Result.failure()
        val displayName = inputData.getString(KEY_DISPLAY_NAME) ?: return Result.failure()

        return ensureUserExistsUseCase.invoke(
            User(
                uid = uid,
                displayName = displayName
            )
        ).fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }

    companion object {
        const val KEY_UID = "uid"
        const val KEY_DISPLAY_NAME = "display_name"
    }
}