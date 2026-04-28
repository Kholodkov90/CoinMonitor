package com.kholodkov.coinmonitor.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kholodkov.coinmonitor.data.local.db.dao.DayDao
import com.kholodkov.coinmonitor.data.local.db.dao.ExchangeRateDao
import com.kholodkov.coinmonitor.data.local.db.dao.OrphanedDao
import com.kholodkov.coinmonitor.data.local.db.dao.PurchaseDao
import com.kholodkov.coinmonitor.data.local.db.dao.TransactionDao
import com.kholodkov.coinmonitor.data.local.db.dao.UserDao
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate.ExchangeRateEntity
import com.kholodkov.coinmonitor.data.local.db.entity.orphaned.OrphanedEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.transaction.TransactionEntity
import com.kholodkov.coinmonitor.data.local.db.entity.user.UserEntity
import com.kholodkov.coinmonitor.data.local.db.typeConverters.BigDecimalConverter
import com.kholodkov.coinmonitor.data.local.db.typeConverters.CurrencyConverter
import com.kholodkov.coinmonitor.data.local.db.typeConverters.InstantConverter
import com.kholodkov.coinmonitor.data.local.db.typeConverters.LocalDateConverter
import com.kholodkov.coinmonitor.data.local.db.typeConverters.LocalTimeConverter
import com.kholodkov.coinmonitor.data.local.db.typeConverters.OrphanedConverter

@Database(
    entities = [
        UserEntity::class,
        DayEntity::class,
        ExchangeRateEntity::class,
        TransactionEntity::class,
        PurchaseEntity::class,
        OrphanedEntity::class,
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)

@TypeConverters(
    BigDecimalConverter::class,
    LocalDateConverter::class,
    LocalTimeConverter::class,
    CurrencyConverter::class,
    InstantConverter::class,
    OrphanedConverter::class,
)
abstract class CoinDb : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getDayDao(): DayDao
    abstract fun getTransactionDao(): TransactionDao
    abstract fun getPurchaseDao(): PurchaseDao
    abstract fun getOrphanedDao(): OrphanedDao
    abstract fun getExchangeRateDao(): ExchangeRateDao
}