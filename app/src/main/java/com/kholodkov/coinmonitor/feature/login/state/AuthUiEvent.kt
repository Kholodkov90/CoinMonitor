package com.kholodkov.coinmonitor.feature.login.state

sealed class AuthUiEvent {
    data object EnterApp : AuthUiEvent()
}