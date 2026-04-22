package com.kholodkov.coinmonitor.data.datasource.transaction

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransaction
import com.kholodkov.coinmonitor.data.model.transaction.RemoteTransactionChange
import com.kholodkov.coinmonitor.data.remote.firestore.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toRemote
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun push(transaction: RemoteTransaction) {
        firestore
            .collection(COLLECTION_DAYS)
            .document(transaction.date.formatForFirestore())
            .collection(COLLECTION_TRANSACTIONS)
            .document(transaction.uid)
            .set(transaction.toFirestore())
    }

    fun delete(transaction: RemoteTransaction) {
        firestore
            .collection(COLLECTION_DAYS)
            .document(transaction.date.formatForFirestore())
            .collection(COLLECTION_TRANSACTIONS)
            .document(transaction.uid)
            .delete()
    }

    fun observeChanges(): Flow<List<RemoteTransactionChange>> =
        firestore.collectionGroup(COLLECTION_TRANSACTIONS)
            .snapshots()
            .map { it.toRemoteTransactionChanges() }

    private fun QuerySnapshot.toRemoteTransactionChanges(): List<RemoteTransactionChange> =
        documentChanges.mapNotNull { documentChange ->
            runCatching {
                val transaction = documentChange.document.toObject(FirestoreTransaction::class.java)

                when (documentChange.type) {
                    DocumentChange.Type.ADDED,
                    DocumentChange.Type.MODIFIED -> RemoteTransactionChange.Upsert(transaction.toRemote())

                    DocumentChange.Type.REMOVED -> RemoteTransactionChange.Delete(transaction.uid)
                }
            }.onFailure {
                Log.e("TransactionRemote", "Parse failed: ${documentChange.document.id}", it)
            }.getOrNull()
        }


    companion object {
        private const val COLLECTION_DAYS = "days"
        private const val COLLECTION_TRANSACTIONS = "transactions"
    }
}