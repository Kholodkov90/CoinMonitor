package com.kholodkov.coinmonitor.domain.usecase.config

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import javax.inject.Inject

class FetchConfigUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
) {
    suspend operator fun invoke() {
        try {
            appConfigRepository.fetchConfig()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}