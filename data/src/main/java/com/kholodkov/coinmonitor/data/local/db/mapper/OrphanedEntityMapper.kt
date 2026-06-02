package com.kholodkov.coinmonitor.data.local.db.mapper

import com.kholodkov.coinmonitor.data.local.db.entity.orphaned.OrphanedEntity
import com.kholodkov.coinmonitor.data.model.orphaned.Orphaned

fun OrphanedEntity.toOrphaned() = Orphaned(
    id = id,
    type = type,
    rawJson = rawJson
)

fun Orphaned.toEntity() = OrphanedEntity(
    id = id,
    type = type,
    rawJson = rawJson
)