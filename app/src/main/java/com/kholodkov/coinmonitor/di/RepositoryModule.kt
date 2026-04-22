package com.kholodkov.coinmonitor.di

import com.kholodkov.coinmonitor.data.repository.AuthRepositoryImpl
import com.kholodkov.coinmonitor.data.repository.ExchangeRepositoryImpl
import com.kholodkov.coinmonitor.data.repository.PreferencesRepositoryImpl
import com.kholodkov.coinmonitor.data.repository.SyncRepositoryImpl
import com.kholodkov.coinmonitor.data.repository.TransactionRepositoryImpl
import com.kholodkov.coinmonitor.data.repository.UserRepositoryImpl
import com.kholodkov.coinmonitor.domain.repository.AuthRepository
import com.kholodkov.coinmonitor.domain.repository.ExchangeRepository
import com.kholodkov.coinmonitor.domain.repository.PreferencesRepository
import com.kholodkov.coinmonitor.domain.repository.SyncRepository
import com.kholodkov.coinmonitor.domain.repository.TransactionRepository
import com.kholodkov.coinmonitor.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(transactionRepository: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(preferencesRepository: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(syncRepository: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    abstract fun bindExchangeRepository(exchangeRepository: ExchangeRepositoryImpl): ExchangeRepository
}