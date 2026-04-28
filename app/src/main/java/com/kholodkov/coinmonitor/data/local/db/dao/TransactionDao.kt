package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.kholodkov.coinmonitor.data.local.db.entity.transaction.FullTransactionEntity
import com.kholodkov.coinmonitor.data.local.db.entity.transaction.TransactionEntity
import com.kholodkov.coinmonitor.data.local.db.entity.transaction.TransactionSyncEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {

    @Query(
        """
            SELECT *
            FROM `transaction` 
            WHERE trn_uid = :uid
            LIMIT 1
        """
    )
    suspend fun getByUid(uid: String): TransactionEntity?

    @Query(
        """
            SELECT trn_id
            FROM `transaction` 
            WHERE trn_uid = :uid
            LIMIT 1
        """
    )
    suspend fun getIdByUid(uid: String): Long?

    @Query(
        """
            SELECT 
                trn_uid AS uid,
                day_date AS date,
                usr_uid AS userUid,
                trn_amount AS amount,
                trn_time AS time,
                trn_currency AS currency, 
                usr_display_name AS userName,
                trn_updated_at as updatedAt
            FROM `transaction` 
                INNER JOIN day ON trn_day_id = day_id
                INNER JOIN user ON trn_user_id = usr_id
            WHERE day_date = :date
            ORDER BY trn_time
        """
    )
    fun observeByDate(
        date: LocalDate
    ): Flow<List<FullTransactionEntity>>

    @Query(
        """
            SELECT 
                trn_uid AS uid,
                day_date AS date,
                usr_uid AS userUid,
                trn_amount AS amount,
                trn_time AS time,
                trn_currency AS currency, 
                usr_display_name AS userName,
                trn_updated_at as updatedAt
            FROM `transaction` 
                INNER JOIN day ON trn_day_id = day_id
                INNER JOIN user ON trn_user_id = usr_id
            WHERE day_date <= :date
        """
    )
    fun observeUpToDate(
        date: LocalDate
    ): Flow<List<FullTransactionEntity>>


    @Query(
        """
            SELECT 
                trn_uid AS uid,
                day_date AS date,
                usr_uid AS userUid,
                trn_amount AS amount,
                trn_time AS time,
                trn_currency AS currency, 
                usr_display_name AS userName,
                trn_updated_at as updatedAt
            FROM `transaction` 
                INNER JOIN day ON trn_day_id = day_id
                INNER JOIN user ON trn_user_id = usr_id
        """
    )
    fun observeAll(): Flow<List<FullTransactionEntity>>

    @Query(
        """
            SELECT 
                trn_uid as uid,
                trn_amount as amount,
                trn_currency as currency,
                trn_time as time,
                trn_updated_at as updatedAt,
                usr_uid as userUid,
                day_date as date
            FROM `transaction` 
                INNER JOIN day ON trn_day_id = day_id
                INNER JOIN user ON trn_user_id = usr_id
            WHERE trn_uid = :uid
        """
    )
    suspend fun getSyncEntity(uid: String): TransactionSyncEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity): Long

    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Query("DELETE FROM `transaction` WHERE trn_uid = :uid")
    suspend fun deleteByUid(uid: String)

}