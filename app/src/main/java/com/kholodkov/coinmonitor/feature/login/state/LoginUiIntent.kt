package com.kholodkov.coinmonitor.feature.login.state

import androidx.credentials.GetCredentialResponse

sealed class LoginUiIntent {
    data object StartLogin: LoginUiIntent()
    data class SignIn(val response: GetCredentialResponse) : LoginUiIntent()
    data object CancelLogin: LoginUiIntent()
    data class LoginError(val exception: Exception): LoginUiIntent()
    data object DismissErrorDialog: LoginUiIntent()
}