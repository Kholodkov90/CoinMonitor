package com.kholodkov.coinmonitor.data.datasource.day

import com.kholodkov.coinmonitor.data.model.day.Day
import java.time.LocalDate
import javax.inject.Inject

class DayDataSource @Inject constructor(
    private val remoteDataSource: DayRemoteDataSource,
    private val localDataSource: DayLocalDataSource
) {
    fun observeRemoteChanges() = remoteDataSource.observeChanges()

    fun observeHasDaysWithoutRate() = localDataSource.observeHasDaysWithoutRate()

    suspend fun getOrCreateDayId(date: LocalDate) = localDataSource.getOrCreateDayId(date)

    suspend fun resolve(day: Day) = localDataSource.resolve(day)

    suspend fun getDaysWithoutRate() = localDataSource.getDaysWithoutRate()

    suspend fun updateRate(day: Day) {
        localDataSource.updateRate(day)
        remoteDataSource.updateRate(day)
    }
}