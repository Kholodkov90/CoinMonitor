package com.kholodkov.coinmonitor.domain.usecase.config

import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import javax.inject.Inject

class FetchConfigUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
) {
    suspend operator fun invoke() = appConfigRepository.fetchConfig()
}