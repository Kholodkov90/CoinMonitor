package com.kholodkov.coinmonitor.data.datasource.purchase

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchase
import com.kholodkov.coinmonitor.data.model.purchase.RemotePurchaseChange
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toRemote
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestorePurchase
import com.kholodkov.coinmonitor.data.remote.firestore.tools.FirestoreCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PurchaseRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun push(purchase: RemotePurchase) {
        firestore
            .collection(FirestoreCollections.PURCHASES)
            .document(purchase.uid)
            .set(purchase.toFirestore())
    }

    fun delete(purchase: RemotePurchase) {
        firestore
            .collection(FirestoreCollections.PURCHASES)
            .document(purchase.uid)
            .delete()
    }

    fun observeChanges(): Flow<List<RemotePurchaseChange>> =
        firestore.collection(FirestoreCollections.PURCHASES)
            .snapshots()
            .map { it.toRemotePurchaseChanges() }

    private fun QuerySnapshot.toRemotePurchaseChanges(): List<RemotePurchaseChange> =
        documentChanges.mapNotNull { documentChange ->
            runCatching {
                val document = documentChange.document
                val uid = document.uid
                val purchase = document.toObject(FirestorePurchase::class.java)

                when (documentChange.type) {
                    DocumentChange.Type.ADDED,
                    DocumentChange.Type.MODIFIED -> RemotePurchaseChange.Upsert(
                        purchase.toRemote(uid)
                    )

                    DocumentChange.Type.REMOVED -> RemotePurchaseChange.Delete(uid)
                }
            }.onFailure {
                Log.e("TransactionRemote", "Parse failed: ${documentChange.document.id}", it)
            }.getOrNull()
        }

    val QueryDocumentSnapshot.uid: String
        get() = this.id
}