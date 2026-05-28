package com.kholodkov.coinmonitor.domain.repository

import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    suspend fun fetchConfig()
    fun observeConfig(): Flow<AppConfig>
}