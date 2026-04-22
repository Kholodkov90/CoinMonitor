package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import com.kholodkov.coinmonitor.data.model.day.Day

fun Day.toEntity() = DayEntity(
    date = date,
    exchangeRate = exchangeRate
)