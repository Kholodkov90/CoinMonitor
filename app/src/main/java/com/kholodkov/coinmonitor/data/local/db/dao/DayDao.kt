package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Query
import androidx.room.Upsert
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DayDao {

    @Query(
        """
        SELECT * 
        FROM day 
        WHERE day_exchange_rate IS NULL 
            AND day_date <= :today
        """
    )
    fun observeDaysWithoutRate(today: LocalDate = LocalDate.now()): Flow<List<DayEntity>>

    @Query("SELECT * FROM day WHERE day_id = :id")
    suspend fun getById(id: Long): DayEntity

    @Query(
        """
        SELECT day_id
        FROM day
        WHERE day_date = :date
        LIMIT 1
    """
    )
    suspend fun getIdByDate(date: LocalDate): Long?

    @Query(
        """
        SELECT *
        FROM day
        WHERE day_date = :date
        LIMIT 1
    """
    )
    suspend fun getByDate(date: LocalDate): DayEntity?

    @Insert(onConflict = ABORT)
    suspend fun insert(day: DayEntity): Long

    @Upsert
    suspend fun upsert(day: DayEntity)
}