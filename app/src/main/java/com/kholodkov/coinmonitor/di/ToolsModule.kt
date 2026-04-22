package com.kholodkov.coinmonitor.di

import com.kholodkov.coinmonitor.data.local.tools.UidGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ToolsModule {
    @Binds
    @Singleton
    abstract fun bindUidGenerator(uidGenerator: UidGenerator.Base): UidGenerator
}