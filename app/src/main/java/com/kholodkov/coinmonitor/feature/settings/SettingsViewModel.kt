package com.kholodkov.coinmonitor.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.usecase.auth.SignOutUseCase
import com.kholodkov.coinmonitor.domain.usecase.settings.ObserveDisplayCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.settings.ObserveDisplayNameUseCase
import com.kholodkov.coinmonitor.domain.usecase.settings.ObserveStartOfWeekUseCase
import com.kholodkov.coinmonitor.domain.usecase.settings.SaveDisplayCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.settings.SaveDisplayNameUseCase
import com.kholodkov.coinmonitor.domain.usecase.settings.SaveStartOfWeekUseCase
import com.kholodkov.coinmonitor.feature.settings.state.SettingsUiEvent
import com.kholodkov.coinmonitor.feature.settings.state.SettingsUiIntent
import com.kholodkov.coinmonitor.feature.settings.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeDisplayNameUseCase: ObserveDisplayNameUseCase,
    observeDisplayCurrencyUseCase: ObserveDisplayCurrencyUseCase,
    observeStartOfWeekUseCase: ObserveStartOfWeekUseCase,
    private val saveDisplayNameUseCase: SaveDisplayNameUseCase,
    private val saveDisplayCurrencyUseCase: SaveDisplayCurrencyUseCase,
    private val saveStartOfWeekUseCase: SaveStartOfWeekUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val isEditNameDialogVisibleState = MutableStateFlow(false)

    val mainInfoState = combine(
        observeDisplayNameUseCase(),
        observeDisplayCurrencyUseCase(),
        observeStartOfWeekUseCase()
    ) { name, currency, startOfWeek ->
        MainInfo(
            name = name,
            currency = currency,
            startOfWeek = startOfWeek
        )
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        mainInfoState,
        isEditNameDialogVisibleState
    ) { mainInfo, isEditNameDialogVisible ->
        SettingsUiState(
            name = mainInfo.name,
            currency = mainInfo.currency,
            startOfWeek = mainInfo.startOfWeek,
            isEditNameDialogVisible = isEditNameDialogVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    private val _events = MutableSharedFlow<SettingsUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: SettingsUiIntent) = when (intent) {
        is SettingsUiIntent.EditName -> handleEditName()
        is SettingsUiIntent.HideEditNameDialog -> handleDiscardNameEditing()
        is SettingsUiIntent.SaveName -> handleSaveName(intent.name)
        is SettingsUiIntent.SelectCurrency -> handleSelectCurrency(intent.currency)
        is SettingsUiIntent.SelectStartOfWeek -> handleSelectStartOfWeek(intent.day)
        is SettingsUiIntent.SignOut -> handleSignOut()
    }

    private fun handleEditName() = isEditNameDialogVisibleState.update { true }

    private fun handleDiscardNameEditing() = isEditNameDialogVisibleState.update { false }

    private fun handleSaveName(name: String) {
        viewModelScope.launch {
            saveDisplayNameUseCase(name)
            isEditNameDialogVisibleState.update { false }
        }
    }

    private fun handleSelectCurrency(currency: Currency) {
        viewModelScope.launch { saveDisplayCurrencyUseCase(currency) }
    }

    private fun handleSelectStartOfWeek(day: DayOfWeek) {
        viewModelScope.launch { saveStartOfWeekUseCase(day) }
    }

    private fun handleSignOut() {
        viewModelScope.launch {
            signOutUseCase()
            _events.emit(SettingsUiEvent.Exit)
        }
    }

    data class MainInfo(
        val name: String = "",
        val currency: Currency = Currency.RSD,
        val startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    )
}