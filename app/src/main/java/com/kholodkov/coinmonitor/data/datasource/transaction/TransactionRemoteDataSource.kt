package com.kholodkov.coinmonitor.data.datasource.transaction

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransactionChange
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toRemote
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreTransaction
import com.kholodkov.coinmonitor.data.remote.firestore.tools.FirestoreCollections
import com.kholodkov.coinmonitor.data.remote.firestore.tools.formatForFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun push(transaction: RemoteTransaction) {
        firestore
            .collection(FirestoreCollections.DAYS)
            .document(transaction.date.formatForFirestore())
            .collection(FirestoreCollections.TRANSACTIONS)
            .document(transaction.uid)
            .set(transaction.toFirestore())
    }

    fun delete(transaction: RemoteTransaction) {
        firestore
            .collection(FirestoreCollections.DAYS)
            .document(transaction.date.formatForFirestore())
            .collection(FirestoreCollections.TRANSACTIONS)
            .document(transaction.uid)
            .delete()
    }

    fun observeChanges(): Flow<List<RemoteTransactionChange>> =
        firestore.collectionGroup(FirestoreCollections.TRANSACTIONS)
            .snapshots()
            .map { it.toRemoteTransactionChanges() }

    private fun QuerySnapshot.toRemoteTransactionChanges(): List<RemoteTransactionChange> =
        documentChanges.mapNotNull { documentChange ->
            runCatching {
                val document = documentChange.document
                val uid = document.uid
                val date = document.date
                val transaction = document.toObject(FirestoreTransaction::class.java)

                when (documentChange.type) {
                    DocumentChange.Type.ADDED,
                    DocumentChange.Type.MODIFIED -> RemoteTransactionChange.Upsert(
                        transaction.toRemote(
                            uid = uid,
                            date = date
                        )
                    )

                    DocumentChange.Type.REMOVED -> RemoteTransactionChange.Delete(uid)
                }
            }.onFailure {
                Log.e("TransactionRemote", "Parse failed: ${documentChange.document.id}", it)
            }.getOrNull()
        }

    val QueryDocumentSnapshot.uid: String
        get() = this.id

    val QueryDocumentSnapshot.date: String
        get() = reference.parent.parent?.id ?: error("No date in path")
}