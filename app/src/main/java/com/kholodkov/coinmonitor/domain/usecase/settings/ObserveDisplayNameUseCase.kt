package com.kholodkov.coinmonitor.domain.usecase.settings

import com.kholodkov.coinmonitor.domain.repository.UserRepository
import javax.inject.Inject

class ObserveDisplayNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.observeDisplayName()
}