package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
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
        sendIntent = { viewModel.onIntent(it) }
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
fun SettingsScreen(
    uiState: SettingsUiState,
    sendIntent: (SettingsUiIntent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Настройки")
        Spacer(modifier = Modifier.height(8.dp))
        NameCard(
            name = uiState.name,
            onClick = { sendIntent(SettingsUiIntent.EditName) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CurrencyCard(
            selectedCurrency = uiState.currency,
            onSelect = { currency -> sendIntent(SettingsUiIntent.SelectCurrency(currency)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        StartOfWeekCard(
            selectedDay = uiState.startOfWeek,
            onDaySelected = { day -> sendIntent(SettingsUiIntent.SelectStartOfWeek(day)) }
        )
        Spacer(modifier = Modifier.weight(1f))
        SignOutCard(
            onClick = { sendIntent(SettingsUiIntent.SignOut) }
        )

        if (uiState.isEditNameDialogVisible) {
            DisplayEditNameDialog(
                uiState = uiState,
                sendIntent = sendIntent
            )
        }
    }
}

@Composable
private fun DisplayEditNameDialog(
    uiState: SettingsUiState,
    sendIntent: (SettingsUiIntent) -> Unit
) {
    EditNameDialog(
        currentName = uiState.name,
        onDismiss = { sendIntent(SettingsUiIntent.HideEditNameDialog) },
        onConfirm = { name -> sendIntent(SettingsUiIntent.SaveName(name)) }
    )
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
        sendIntent = {}
    )
}