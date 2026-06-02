package com.kholodkov.coinmonitor.domain.usecase.sync

import com.kholodkov.coinmonitor.domain.repository.SyncRepository
import javax.inject.Inject

class SyncUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke() = syncRepository.sync()
}