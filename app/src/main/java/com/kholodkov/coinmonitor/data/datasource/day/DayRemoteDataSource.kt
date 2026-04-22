package com.kholodkov.coinmonitor.data.datasource.day

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.model.day.Day
import com.kholodkov.coinmonitor.data.remote.firestore.formatForFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toDay
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DayRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun updateRate(day: Day) {
        firestore
            .collection(COLLECTION_DAYS)
            .document(day.date.formatForFirestore())
            .set(day.toFirestore())
    }

    fun observeChanges(): Flow<List<Day>> =
        firestore.collection(COLLECTION_DAYS)
            .snapshots()
            .map { it.toDays() }

    private fun QuerySnapshot.toDays(): List<Day> =
        documentChanges.mapNotNull { documentChange ->
            runCatching {
                documentChange.document.toObject(FirestoreDay::class.java).toDay()
            }.onFailure {
                Log.e("TransactionRemote", "Parse failed: ${documentChange.document.id}", it)
            }.getOrNull()
        }

    companion object {
        private const val COLLECTION_DAYS = "days"
    }
}