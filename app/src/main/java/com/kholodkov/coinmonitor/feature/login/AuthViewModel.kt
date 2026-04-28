package com.kholodkov.coinmonitor.feature.login

import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.domain.usecase.auth.SignInUseCase
import com.kholodkov.coinmonitor.feature.login.state.AuthUiEvent
import com.kholodkov.coinmonitor.feature.login.state.AuthUiIntent
import com.kholodkov.coinmonitor.feature.login.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthUiEvent>()
    val events = _events.asSharedFlow()

    fun onUiIntent(intent: AuthUiIntent) = when (intent) {
        is AuthUiIntent.SignIn -> handleSignIn(intent.response)
    }

    fun handleSignIn(response: GetCredentialResponse) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            signInUseCase(response).fold(
                onSuccess = {
                    _events.emit(AuthUiEvent.EnterApp)
                    _uiState.value = AuthUiState.Idle
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message)
                }
            )
        }
    }
}

