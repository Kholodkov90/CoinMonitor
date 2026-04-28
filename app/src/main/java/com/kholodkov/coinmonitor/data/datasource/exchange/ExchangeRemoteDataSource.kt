package com.kholodkov.coinmonitor.data.datasource.exchange

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.kholodkov.coinmonitor.data.remote.exchange.ExchangeApi
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toExchangeRate
import com.kholodkov.coinmonitor.data.remote.firestore.mapper.toFirestore
import com.kholodkov.coinmonitor.data.remote.firestore.model.FirestoreCurrency
import com.kholodkov.coinmonitor.data.remote.firestore.tools.FirestoreCollections
import com.kholodkov.coinmonitor.data.remote.firestore.tools.formatForFirestore
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ExchangeRemoteDataSource @Inject constructor(
    private val api: ExchangeApi,
    private val firestore: FirebaseFirestore
) {
    suspend fun getEurToRsd(date: LocalDate): BigDecimal? = runCatching {
        api.getRate(date.toString()).rate
    }.getOrNull()


    fun observeChanges(): Flow<List<ExchangeRate>> =
        firestore.collectionGroup(FirestoreCollections.CURRENCIES)
            .snapshots()
            .map { it.toDayRates() }

    private fun QuerySnapshot.toDayRates(): List<ExchangeRate> =
        documentChanges.mapNotNull { documentChange ->
            val document = documentChange.document
            val date = document.date
            val currency = document.currency
            document.toObject(FirestoreCurrency::class.java).toExchangeRate(date, currency)
        }

    fun push(exchangeRate: ExchangeRate) {
        firestore
            .collection(FirestoreCollections.DAYS)
            .document(exchangeRate.date.formatForFirestore())
            .collection(FirestoreCollections.CURRENCIES)
            .document(exchangeRate.currency.name)
            .set(exchangeRate.toFirestore())
    }

    val QueryDocumentSnapshot.date: String
        get() = reference.parent.parent?.id ?: error("No date in path")

    val QueryDocumentSnapshot.currency: Currency
        get() = Currency.valueOf(this.id)
}