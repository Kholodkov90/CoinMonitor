package com.kholodkov.coinmonitor.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kholodkov.coinmonitor.domain.model.Currency
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    override suspend fun setInputCurrency(currency: Currency) {
        dataStore.edit { prefs ->
            prefs[INPUT_CURRENCY] = currency.name
        }
    }

    override fun observeInputCurrency(): Flow<Currency> {
        return dataStore.data.map { prefs ->
            val saved = prefs[INPUT_CURRENCY]
            Currency.entries.find { it.name == saved } ?: Currency.RSD
        }
    }

    override fun observeDisplayCurrency(): Flow<Currency> {
        return dataStore.data.map { prefs ->
            val saved = prefs[DISPLAY_CURRENCY]
            Currency.entries.find { it.name == saved } ?: Currency.RSD
        }
    }

    private companion object {
        val INPUT_CURRENCY = stringPreferencesKey("input_currency")
        val DISPLAY_CURRENCY = stringPreferencesKey("display_currency")
    }
}