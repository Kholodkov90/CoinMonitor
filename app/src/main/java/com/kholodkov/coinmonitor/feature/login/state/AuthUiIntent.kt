package com.kholodkov.coinmonitor.feature.login.state

import androidx.credentials.GetCredentialResponse

sealed class AuthUiIntent {
    data class SignIn(val response: GetCredentialResponse) : AuthUiIntent()
}