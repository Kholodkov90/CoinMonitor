package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate.ExchangeRateEntity
import com.kholodkov.coinmonitor.data.local.db.entity.exchangeRate.FullExchangeRateEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExchangeRateDao {
    @Query(
        """
            SELECT EXISTS (
                SELECT 1 
                FROM day 
                    LEFT JOIN exchange_rate ON day_id = exr_day_id 
                WHERE day_date <= :date
                    AND exr_id IS NULL
            )
        """
    )
    fun observeHasMissingRates(date: LocalDate = LocalDate.now()): Flow<Boolean>

    @Query(
        """
            SELECT 
                day_date AS date,
                exr_currency AS currency,
                exr_rate AS exchangeRate
            FROM exchange_rate 
                INNER JOIN day ON exr_day_id = day_id
        """
    )
    fun observeAll(): Flow<List<FullExchangeRateEntity>>


    @Query(
        """
            SELECT 
                day_date AS date,
                exr_currency AS currency,
                exr_rate AS exchangeRate
            FROM exchange_rate 
                INNER JOIN day ON exr_day_id = day_id
            WHERE exr_id = :id
        """
    )
    suspend fun getById(id: Long): FullExchangeRateEntity

    @Query(
        """
            SELECT day_date 
            FROM day 
                LEFT JOIN exchange_rate ON day_id = exr_day_id 
            WHERE day_date <= :date
                AND exr_id IS NULL
        """
    )
    suspend fun getDatesWithMissingRates(date: LocalDate = LocalDate.now()): List<LocalDate>

    @Insert(onConflict = IGNORE)
    suspend fun insertIfNotExists(exchangeRate: ExchangeRateEntity) : Long
}