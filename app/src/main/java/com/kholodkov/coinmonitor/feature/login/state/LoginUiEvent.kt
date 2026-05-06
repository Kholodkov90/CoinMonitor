package com.kholodkov.coinmonitor.feature.login.state

sealed class LoginUiEvent {
    data object EnterApp : LoginUiEvent()
}