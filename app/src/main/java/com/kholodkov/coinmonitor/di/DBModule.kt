package com.kholodkov.coinmonitor.di

import android.content.Context
import androidx.room.Room
import com.kholodkov.coinmonitor.data.local.db.CoinDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext context: Context
    ): CoinDb {
        return Room.databaseBuilder(
            context,
            CoinDb::class.java,
            "coin_db"
        )
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .build()
    }

    @Provides
    fun provideUserDao(db: CoinDb) = db.getUserDao()

    @Provides
    fun provideDayDao(db: CoinDb) = db.getDayDao()

    @Provides
    fun provideTransactionDao(db: CoinDb) = db.getTransactionDao()

    @Provides
    fun providePurchaseDao(db: CoinDb) = db.getPurchaseDao()

    @Provides
    fun provideOrphanedDao(db: CoinDb) = db.getOrphanedDao()

}