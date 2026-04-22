package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Query
import androidx.room.Upsert
import com.kholodkov.coinmonitor.data.local.db.entity.user.UserEntity

@Dao
interface UserDao {

    @Query("SELECT usr_id FROM user WHERE usr_uid = :uid LIMIT 1")
    suspend fun getIdByUid(uid: String): Long?

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Insert(onConflict = ABORT)
    suspend fun insert(user: UserEntity): Long
}