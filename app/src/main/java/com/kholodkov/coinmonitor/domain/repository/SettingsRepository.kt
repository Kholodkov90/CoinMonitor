package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface SettingsRepository {
    fun observeInputCurrency(): Flow<Currency>
    fun observeDisplayCurrency(): Flow<Currency>
    fun observeStartOfWeek(): Flow<DayOfWeek>
    suspend fun setInputCurrency(currency: Currency)
    suspend fun setDisplayCurrency(currency: Currency)
    suspend fun setStartOfWeek(day: DayOfWeek)
}