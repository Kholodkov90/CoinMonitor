package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun setInputCurrency(currency: Currency)
    fun observeInputCurrency(): Flow<Currency>
    fun observeDisplayCurrency(): Flow<Currency>
}