package com.kholodkov.coinmonitor.data.local.db.entity.orphaned

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kholodkov.coinmonitor.data.datasource.orphaned.OrphanedType

@Entity(tableName = "orphaned")
data class OrphanedEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orp_id")
    val id: Long = 0,
    @ColumnInfo(name = "orp_type")
    val type: OrphanedType,
    @ColumnInfo(name = "orp_raw_json")
    val rawJson: String,
)