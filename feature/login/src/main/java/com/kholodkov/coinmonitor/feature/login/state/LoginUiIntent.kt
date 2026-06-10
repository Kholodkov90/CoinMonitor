package com.kholodkov.coinmonitor.feature.login.state

sealed class LoginUiIntent {
    data object StartLogin : LoginUiIntent()
    data class SignIn(val idToken: String) : LoginUiIntent()
    data object CancelLogin : LoginUiIntent()
    data class LoginError(val exception: Exception) : LoginUiIntent()
    data object DismissErrorDialog : LoginUiIntent()
}