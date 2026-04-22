package com.kholodkov.coinmonitor.feature.settings.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kholodkov.coinmonitor.feature.settings.SettingsViewModel
import com.kholodkov.coinmonitor.feature.settings.ui.state.SettingsUiEvent
import com.kholodkov.coinmonitor.feature.settings.ui.state.SettingsUiIntent

@Composable
fun SettingsScreenRoute(
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreen(
        sendIntent = { viewModel.onIntent(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when(event){
                SettingsUiEvent.Exit -> onSignOut()
            }
        }
    }
}

@Composable
fun SettingsScreen(
    sendIntent: (SettingsUiIntent) -> Unit
) {
    Column() {
        Text("Settings")
        Button(
            onClick = { sendIntent(SettingsUiIntent.SignOut) }
        ) {

            Text("Sing out")
        }
    }
}