package com.kholodkov.coinmonitor.data.datasource.purchase

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchaseChange
import com.kholodkov.coinmonitor.data.remote.firestore.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toRemote
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestorePurchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PurchaseRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun push(purchase: RemotePurchase) {
        firestore
            .collection(COLLECTION_DAYS)
            .document(purchase.date.formatForFirestore())
            .collection(COLLECTION_PURCHASE)
            .document(purchase.uid)
            .set(purchase.toFirestore())
    }

    fun delete(purchase: RemotePurchase) {
        firestore
            .collection(COLLECTION_DAYS)
            .document(purchase.date.formatForFirestore())
            .collection(COLLECTION_PURCHASE)
            .document(purchase.uid)
            .delete()
    }

    fun observeChanges(): Flow<List<RemotePurchaseChange>> =
        firestore.collectionGroup(COLLECTION_PURCHASE)
            .snapshots()
            .map { it.toRemotePurchaseChanges() }

    private fun QuerySnapshot.toRemotePurchaseChanges(): List<RemotePurchaseChange> =
        documentChanges.mapNotNull { documentChange ->
            runCatching {
                val purchase = documentChange.document.toObject(FirestorePurchase::class.java)

                when (documentChange.type) {
                    DocumentChange.Type.ADDED,
                    DocumentChange.Type.MODIFIED -> RemotePurchaseChange.Upsert(purchase.toRemote())

                    DocumentChange.Type.REMOVED -> RemotePurchaseChange.Delete(purchase.uid)
                }
            }.onFailure {
                Log.e("TransactionRemote", "Parse failed: ${documentChange.document.id}", it)
            }.getOrNull()
        }

    companion object {
        private const val COLLECTION_DAYS = "days"
        private const val COLLECTION_PURCHASE = "purchase"
    }
}