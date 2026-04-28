package com.kholodkov.coinmonitor.domain.usecase.auth

import android.util.Log
import androidx.credentials.GetCredentialResponse
import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(response: GetCredentialResponse) = runCatching {
        authRepository.signIn(response.credential).getOrThrow()
    }.onFailure {
        Log.e("SignInWithGoogleUseCase", "Error", it)
    }
}