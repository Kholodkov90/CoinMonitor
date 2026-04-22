package com.kholodkov.coinmonitor.data.model.orphaned

import com.kholodkov.coinmonitor.data.datasource.orphaned.OrphanedType

data class Orphaned(
    val id: Long = 0,
    val type: OrphanedType,
    val rawJson: String,
)