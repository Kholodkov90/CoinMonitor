package com.kholodkov.coinmonitor.domain.usecase.auth

import androidx.credentials.GetCredentialResponse
import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(response: GetCredentialResponse) =
        authRepository.signIn(response.credential)
}