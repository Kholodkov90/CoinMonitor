package com.kholodkov.coinmonitor.feature.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.kholodkov.coinmonitor.BuildConfig

object CredentialManagerUtils {
    suspend fun signInWithGoogle(context: Context): GetCredentialResponse {
        val credentialManager = CredentialManager.create(context)
        return credentialManager.getCredential(
            context = context,
            request = request
        )
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
}