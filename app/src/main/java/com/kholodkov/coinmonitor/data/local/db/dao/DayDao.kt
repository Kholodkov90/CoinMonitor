package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import java.time.LocalDate

@Dao
interface DayDao {

    @Query(
        """
        SELECT day_id
        FROM day
        WHERE day_date = :date
        LIMIT 1
    """
    )
    suspend fun getIdByDate(date: LocalDate): Long?

    @Insert(onConflict = IGNORE)
    suspend fun insert(day: DayEntity): Long
}