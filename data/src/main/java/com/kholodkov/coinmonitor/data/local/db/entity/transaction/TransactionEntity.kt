package com.kholodkov.coinmonitor.data.local.db.entity.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kholodkov.coinmonitor.data.local.db.entity.day.DayEntity
import com.kholodkov.coinmonitor.data.local.db.entity.user.UserEntity
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalTime

@Entity(
    tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["day_id"],
            childColumns = ["trn_day_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["usr_id"],
            childColumns = ["trn_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("trn_uid", unique = true),
        Index("trn_day_id"),
        Index("trn_user_id")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trn_id")
    val id: Long = 0,
    @ColumnInfo(name = "trn_uid")
    val uid: String,
    @ColumnInfo(name = "trn_user_id")
    val userId: Long,
    @ColumnInfo(name = "trn_day_id")
    val dayId: Long,
    @ColumnInfo(name = "trn_amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "trn_currency")
    val currency: Currency,
    @ColumnInfo(name = "trn_time")
    val time: LocalTime,
    @ColumnInfo(name = "trn_updated_at")
    val updatedAt: Instant
)