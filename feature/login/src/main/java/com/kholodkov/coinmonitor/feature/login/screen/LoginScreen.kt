package com.kholodkov.coinmonitor.feature.login.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kholodkov.coinmonitor.core.ui.dialogs.ErrorDialog
import com.kholodkov.coinmonitor.feature.login.BuildConfig
import com.kholodkov.coinmonitor.feature.login.LoginViewModel
import com.kholodkov.coinmonitor.feature.login.R
import com.kholodkov.coinmonitor.feature.login.state.LoginUiEvent
import com.kholodkov.coinmonitor.feature.login.state.LoginUiIntent
import com.kholodkov.coinmonitor.feature.login.state.LoginUiState
import kotlinx.coroutines.launch
import com.kholodkov.coinmonitor.core.ui.R as CoreR

@Composable
fun LoginScreenRoute(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = uiState,
        onIntent = { viewModel.onIntent(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.EnterApp -> {
                    onLoginSuccess()
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onIntent: (LoginUiIntent) -> Unit
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_login_logo),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(CoreR.string.common_app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(64.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    onIntent(LoginUiIntent.StartLogin)
                    try {
                        onIntent(LoginUiIntent.SignIn(getGoogleIdToken(context)))
                    } catch (e: Exception) {
                        onIntent(LoginUiIntent.LoginError(e))
                    }
                }
            },
            enabled = uiState !is LoginUiState.Loading,
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.sign_in),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (uiState is LoginUiState.Error) {
        ErrorDialog(
            message = stringResource(R.string.error_sign_in)
        ) {
            onIntent(LoginUiIntent.DismissErrorDialog)
        }
    }
}

private suspend fun getGoogleIdToken(context: Context): String {
    val response = CredentialManager.create(context).getCredential(
        context = context,
        request = request
    )
    return GoogleIdTokenCredential.createFrom(response.credential.data).idToken
}

private val request: GetCredentialRequest by lazy {
    GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()
}

@Composable
@Preview(showBackground = true)
private fun LoginScreenPreview() {
    LoginScreen(
        uiState = LoginUiState.Idle,
        onIntent = {}
    )
}