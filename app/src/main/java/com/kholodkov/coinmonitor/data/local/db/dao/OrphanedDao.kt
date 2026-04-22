package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kholodkov.coinmonitor.data.local.db.entity.orphaned.OrphanedEntity

@Dao
interface OrphanedDao {

    @Query(
        """
            SELECT * 
            FROM orphaned
        """
    )
    suspend fun getAll(): List<OrphanedEntity>

    @Insert
    suspend fun insert(entity: OrphanedEntity)

    @Query(
        """
            DELETE FROM orphaned 
            WHERE orp_id = :id
            """
    )
    suspend fun delete(id: Long)
}