package com.kholodkov.coinmonitor.core.platform.di

import com.kholodkov.coinmonitor.domain.scheduler.LoadExchangeRateScheduler
import com.kholodkov.coinmonitor.domain.scheduler.SyncUserScheduler
import com.kholodkov.coinmonitor.core.platform.rate.LoadExchangeRateManager
import com.kholodkov.coinmonitor.core.platform.user.SyncUserManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerModule {
    @Binds
    @Singleton
    abstract fun bindLoadRateScheduler(impl: LoadExchangeRateManager): LoadExchangeRateScheduler


    @Binds
    @Singleton
    abstract fun bindSyncUserScheduler(impl: SyncUserManager): SyncUserScheduler
}