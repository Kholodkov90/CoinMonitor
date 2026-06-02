package com.kholodkov.coinmonitor.domain.usecase.settings

import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import java.time.DayOfWeek
import javax.inject.Inject

class SaveStartOfWeekUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(day: DayOfWeek) {
        settingsRepository.setStartOfWeek(day)
    }
}