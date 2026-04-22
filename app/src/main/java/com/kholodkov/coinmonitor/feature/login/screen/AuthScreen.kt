package com.kholodkov.coinmonitor.feature.login.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kholodkov.coinmonitor.feature.login.AuthViewModel
import com.kholodkov.coinmonitor.feature.login.CredentialManagerUtils
import com.kholodkov.coinmonitor.feature.login.state.AuthUiEvent
import com.kholodkov.coinmonitor.feature.login.state.AuthUiIntent
import com.kholodkov.coinmonitor.feature.login.state.AuthUiState
import kotlinx.coroutines.launch

@Composable
fun AuthScreenRoute(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AuthScreen(
        uiState = uiState,
        onClickSingIn = {
            scope.launch {
                viewModel.onUiIntent(
                    AuthUiIntent.SignIn(CredentialManagerUtils.signInWithGoogle(context))
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthUiEvent.EnterApp -> {
                    onLoginSuccess()
                }
            }
        }
    }
}

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onClickSingIn: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onClickSingIn,
            enabled = uiState != AuthUiState.Loading

        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Войти")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AuthScreenPreview() {
    AuthScreen(
        uiState = AuthUiState.Idle,
        onClickSingIn = {}
    )
}



