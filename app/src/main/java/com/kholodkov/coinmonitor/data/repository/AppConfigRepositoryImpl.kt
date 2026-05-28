package com.kholodkov.coinmonitor.data.repository

import com.kholodkov.coinmonitor.data.datasource.config.ConfigDataSource
import com.kholodkov.coinmonitor.domain.repository.AppConfigRepository
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val configDataSource: ConfigDataSource,
) : AppConfigRepository {

    override suspend fun fetchConfig() = configDataSource.fetch()

    override fun observeConfig() = configDataSource.observeAppConfig()

}