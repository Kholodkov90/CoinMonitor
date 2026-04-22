package com.kholodkov.coinmonitor.data.local.db.entity.purchase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import com.kholodkov.coinmonitor.domain.model.Currency
import java.math.BigDecimal
import java.time.Instant

@Entity(
    tableName = "purchase",
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["day_id"],
            childColumns = ["prs_day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("prs_uid", unique = true),
        Index("prs_day_id")
    ]
)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "prs_id")
    val id: Long = 0,
    @ColumnInfo(name = "prs_uid")
    val uid: String,
    @ColumnInfo(name = "prs_day_id")
    val dayId: Long,
    @ColumnInfo(name = "prs_user_id")
    val userId: Long,
    @ColumnInfo(name = "prs_amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "prs_currency")
    val currency: Currency,
    @ColumnInfo(name = "prs_description")
    val description: String,
    @ColumnInfo(name = "prs_updated_at")
    val updatedAt: Instant
)