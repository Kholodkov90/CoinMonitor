package com.kholodkov.coinmonitor.domain.usecase.settings

import com.kholodkov.coinmonitor.domain.repository.UserRepository
import javax.inject.Inject

class SaveDisplayNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String) {
        userRepository.setDisplayName(name)
    }
}