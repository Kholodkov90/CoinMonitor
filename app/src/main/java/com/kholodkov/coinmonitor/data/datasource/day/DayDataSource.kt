package com.kholodkov.coinmonitor.data.datasource.day

import com.kholodkov.coinmonitor.data.local.db.dao.DayDao
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import java.time.LocalDate
import javax.inject.Inject

class DayDataSource @Inject constructor(
    private val dayDao: DayDao
) {
    suspend fun getOrCreateDayId(date: LocalDate): Long {
        dayDao.insert(DayEntity(date = date))
        return dayDao.getIdByDate(date) ?: error("No date $date after insert")
    }
}