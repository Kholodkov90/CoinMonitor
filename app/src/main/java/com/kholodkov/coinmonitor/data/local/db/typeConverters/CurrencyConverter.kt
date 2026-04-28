package com.kholodkov.coinmonitor.data.local.db.typeConverters

import androidx.room.TypeConverter
import com.kholodkov.coinmonitor.domain.model.currency.Currency

class CurrencyConverter {
    @TypeConverter
    fun fromCurrency(value: Currency) = value.name

    @TypeConverter
    fun toCurrency(value: String) = Currency.valueOf(value)
}