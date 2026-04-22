package com.kholodkov.coinmonitor.data.local.db.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
    indices = [
        Index("usr_uid", unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "usr_id")
    val id: Long = 0,
    @ColumnInfo(name = "usr_uid")
    val uid: String,
    @ColumnInfo(name = "usr_display_name")
    val name: String
)