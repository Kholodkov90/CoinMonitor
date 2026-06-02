package com.kholodkov.coinmonitor.domain.repository

interface SyncRepository {
    suspend fun sync()
}