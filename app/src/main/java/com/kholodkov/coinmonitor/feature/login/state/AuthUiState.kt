package com.kholodkov.coinmonitor.feature.login.state

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String?) : AuthUiState
}