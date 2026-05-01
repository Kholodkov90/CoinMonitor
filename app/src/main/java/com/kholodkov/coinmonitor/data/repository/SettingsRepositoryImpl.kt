package com.kholodkov.coinmonitor.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
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

    override fun observeStartOfWeek(): Flow<DayOfWeek> {
        return dataStore.data.map { prefs ->
            prefs[START_OF_WEEK]?.let { DayOfWeek.of(it) } ?: DayOfWeek.MONDAY
        }
    }

    override suspend fun setInputCurrency(currency: Currency) {
        dataStore.edit { prefs ->
            prefs[INPUT_CURRENCY] = currency.name
        }
    }

    override suspend fun setDisplayCurrency(currency: Currency) {
        dataStore.edit { prefs ->
            prefs[DISPLAY_CURRENCY] = currency.name
        }
    }

    override suspend fun setStartOfWeek(day: DayOfWeek) {
        dataStore.edit { prefs ->
            prefs[START_OF_WEEK] = day.value
        }
    }

    private companion object {
        val INPUT_CURRENCY = stringPreferencesKey("input_currency")
        val DISPLAY_CURRENCY = stringPreferencesKey("display_currency")
        val START_OF_WEEK = intPreferencesKey("start_of_week")
    }
}