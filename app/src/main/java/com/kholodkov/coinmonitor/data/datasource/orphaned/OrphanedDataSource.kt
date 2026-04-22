package com.kholodkov.coinmonitor.data.datasource.orphaned

import com.kholodkov.coinmonitor.data.local.db.dao.OrphanedDao
import com.kholodkov.coinmonitor.data.local.db.mapper.toOrphaned
import com.kholodkov.coinmonitor.data.local.db.mapper.toEntity
import com.kholodkov.coinmonitor.data.model.orphaned.Orphaned
import javax.inject.Inject

class OrphanedDataSource @Inject constructor(
    private val orphanedDao: OrphanedDao
) {
    suspend fun getAll() = orphanedDao.getAll().map { it.toOrphaned() }

    suspend fun insert(data: Orphaned) =
        orphanedDao.insert(data.toEntity())

    suspend fun delete(id: Long) = orphanedDao.delete(id)
}