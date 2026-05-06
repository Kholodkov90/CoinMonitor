package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.feature.settings.SettingsViewModel
import com.kholodkov.coinmonitor.feature.settings.state.SettingsUiEvent
import com.kholodkov.coinmonitor.feature.settings.state.SettingsUiIntent
import com.kholodkov.coinmonitor.feature.settings.state.SettingsUiState
import java.time.DayOfWeek

@Composable
fun SettingsScreenRoute(
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onIntent = { viewModel.onIntent(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SettingsUiEvent.Exit -> onSignOut()
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onIntent: (SettingsUiIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        NameCard(
            name = uiState.name,
            onClick = { onIntent(SettingsUiIntent.EditName) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CurrencyCard(
            selectedCurrency = uiState.currency,
            onSelect = { currency -> onIntent(SettingsUiIntent.SelectCurrency(currency)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        StartOfWeekCard(
            selectedDay = uiState.startOfWeek,
            onDaySelected = { day -> onIntent(SettingsUiIntent.SelectStartOfWeek(day)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SignOutCard(
            onClick = { onIntent(SettingsUiIntent.SignOut) }
        )

        if (uiState.isEditNameDialogVisible) {
            EditNameDialog(
                currentName = uiState.name,
                onDismiss = { onIntent(SettingsUiIntent.HideEditNameDialog) },
                onConfirm = { name -> onIntent(SettingsUiIntent.SaveName(name)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        uiState = SettingsUiState(
            name = "Сергей",
            currency = Currency.EUR,
            startOfWeek = DayOfWeek.SUNDAY
        ),
        onIntent = {}
    )
}