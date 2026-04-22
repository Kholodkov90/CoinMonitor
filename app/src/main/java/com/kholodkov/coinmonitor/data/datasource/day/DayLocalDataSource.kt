package com.kholodkov.coinmonitor.data.datasource.day

import com.kholodkov.coinmonitor.data.local.db.dao.DayDao
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import com.kholodkov.coinmonitor.data.local.db.mapper.toDay
import com.kholodkov.coinmonitor.data.local.db.mapper.toEntity
import com.kholodkov.coinmonitor.data.model.day.Day
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class DayLocalDataSource @Inject constructor(
    private val dayDao: DayDao
) {

    fun observeHasDaysWithoutRate() = dayDao.observeDaysWithoutRate()
        .map { it.isNotEmpty() }
        .distinctUntilChanged()

    suspend fun resolve(day: Day) {
        val existing = dayDao.getByDate(day.date)
        val entity = day.toEntity()
        if (existing == null) {
            dayDao.insert(entity)
        } else if (existing.exchangeRate == null && entity.exchangeRate != null) {
            dayDao.upsert(entity.copy(id = existing.id))
        }
    }

    suspend fun getOrCreateDayId(date: LocalDate): Long {
        dayDao.getIdByDate(date)?.let { return it }
        return dayDao.insert(DayEntity(date = date))
    }

    suspend fun getDaysWithoutRate(): List<Day> =
        dayDao.observeDaysWithoutRate().first().map { it.toDay() }

    suspend fun updateRate(day: Day) {
        val existing = dayDao.getByDate(day.date)
            ?: error("No day ${day.date}")
        dayDao.upsert(existing.copy(exchangeRate = day.exchangeRate))
    }
}