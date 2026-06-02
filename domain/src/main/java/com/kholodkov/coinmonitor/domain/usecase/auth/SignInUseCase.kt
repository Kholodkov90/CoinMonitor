package com.kholodkov.coinmonitor.domain.usecase.auth

import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String) = authRepository.signIn(idToken)
}