package com.kholodkov.coinmonitor.data.local.db.typeConverters

import androidx.room.TypeConverter
import com.kholodkov.coinmonitor.data.datasource.orphaned.OrphanedType

class OrphanedConverter {
    @TypeConverter
    fun fromOrphanedType(value: OrphanedType) = value.name

    @TypeConverter
    fun toOrphanedType(value: String) = OrphanedType.valueOf(value)
}