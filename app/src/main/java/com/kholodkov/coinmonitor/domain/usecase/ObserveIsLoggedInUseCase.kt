package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import javax.inject.Inject

class ObserveIsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() = authRepository.observeIsLoggedIn()
}