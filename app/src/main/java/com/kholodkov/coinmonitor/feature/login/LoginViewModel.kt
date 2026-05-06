package com.kholodkov.coinmonitor.feature.login

import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.domain.usecase.auth.SignInUseCase
import com.kholodkov.coinmonitor.feature.login.state.LoginUiEvent
import com.kholodkov.coinmonitor.feature.login.state.LoginUiIntent
import com.kholodkov.coinmonitor.feature.login.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: LoginUiIntent) = when (intent) {
        is LoginUiIntent.StartLogin -> handleStartAuth()
        is LoginUiIntent.SignIn -> handleSignIn(intent.response)
        is LoginUiIntent.CancelLogin -> handleCancelAuth()
        is LoginUiIntent.LoginError -> handleAuthError(intent.exception)
        is LoginUiIntent.DismissErrorDialog -> handleHideErrorDialog()
    }

    private fun handleStartAuth() = _uiState.update { LoginUiState.Loading }

    private fun handleSignIn(response: GetCredentialResponse) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            signInUseCase(response).fold(
                onSuccess = {
                    _events.emit(LoginUiEvent.EnterApp)
                },
                onFailure = {
                    _uiState.value = LoginUiState.Error
                }
            )
        }
    }

    private fun handleCancelAuth() = _uiState.update { LoginUiState.Idle }

    private fun handleAuthError(exception: Exception) {
        if (exception is GetCredentialCancellationException) {
            _uiState.update { LoginUiState.Idle }
        } else {
            _uiState.update { LoginUiState.Error }
        }
    }

    private fun handleHideErrorDialog() = _uiState.update { LoginUiState.Idle }
}

