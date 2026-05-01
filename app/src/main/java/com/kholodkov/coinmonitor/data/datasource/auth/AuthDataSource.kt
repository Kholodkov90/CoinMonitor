package com.kholodkov.coinmonitor.data.datasource.auth

import androidx.credentials.Credential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kholodkov.coinmonitor.data.remote.firebaseAuth.mapper.toUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signIn(credential: Credential) = runCatching {
        val firebaseCredential = GoogleAuthProvider.getCredential(
            GoogleIdTokenCredential.createFrom(credential.data).idToken,
            null
        )
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        authResult.user?.toUser() ?: error("FirebaseUser is null after sign in")
    }

    fun observeIsLoggedIn(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    fun getCurrentUser() = firebaseAuth.currentUser?.toUser() ?: error("User is not logged in")
    fun signOut() = firebaseAuth.signOut()
}