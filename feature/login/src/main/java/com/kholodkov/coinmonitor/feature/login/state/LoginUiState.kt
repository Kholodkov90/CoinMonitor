package com.kholodkov.coinmonitor.feature.login.state

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Error : LoginUiState
}