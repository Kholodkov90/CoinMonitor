package com.kholodkov.coinmonitor.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.domain.usecase.SignOutUseCase
import com.kholodkov.coinmonitor.feature.settings.ui.state.SettingsUiEvent
import com.kholodkov.coinmonitor.feature.settings.ui.state.SettingsUiIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<SettingsUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: SettingsUiIntent) = when (intent) {
        SettingsUiIntent.SignOut -> handleSignOut()
    }

    private fun handleSignOut() {
        viewModelScope.launch {
            signOutUseCase()
            _events.emit(SettingsUiEvent.Exit)
        }
    }
}