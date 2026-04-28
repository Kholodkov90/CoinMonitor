package com.kholodkov.coinmonitor.domain.usecase.auth

import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() = authRepository.signOut()
}