package com.kholodkov.coinmonitor.data.datasource.user


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toUser
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreUser
import com.kholodkov.coinmonitor.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun update(user: User) {
        firestore
            .collection(COLLECTION_USERS)
            .document(user.uid)
            .set(user.toFirestore())
    }

    fun observeChanges(): Flow<List<User>> =
        firestore.collectionGroup(COLLECTION_USERS)
            .snapshots()
            .map { it.toUsers() }

    private fun QuerySnapshot.toUsers(): List<User> =
        documentChanges.mapNotNull { documentChange ->
            runCatching {
                documentChange.document.toObject(FirestoreUser::class.java).toUser()
            }.onFailure {
                Log.e("UserRemote", "Parse failed: ${documentChange.document.id}", it)
            }.getOrNull()
        }

    suspend fun ensureUserExists(user: User) {
        val existing = firestore.collection(COLLECTION_USERS).document(user.uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(existing)
            if (!snapshot.exists()) {
                transaction.set(existing, user.toFirestore())
            }
        }.await()
    }

    companion object {
        private const val COLLECTION_USERS = "users"
    }
}