package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.FullPurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PlannedPurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseEntity
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.PurchaseSyncEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Query(
        """
            SELECT
                prs_uid AS uid,
                day_date AS date,
                usr_uid AS userUid,
                prs_amount AS amount,
                trn_uid AS transactionUid,
                prs_currency AS currency,
                prs_description AS description,
                usr_display_name AS userName,
                prs_updated_at AS updatedAt
            FROM purchase 
                INNER JOIN day ON prs_day_id = day_id
                INNER JOIN user ON prs_user_id = usr_id
                LEFT JOIN `transaction` ON prs_transaction_id = trn_id
            ORDER BY day_date, prs_updated_at
        """
    )
    fun observeAll(): Flow<List<FullPurchaseEntity>>


    @Query(
        """
            SELECT
                prs_amount AS amount,
                prs_currency AS currency,
                day_date as date
            FROM purchase 
                INNER JOIN day ON prs_day_id = day_id
            WHERE prs_transaction_id IS NULL
        """
    )
    fun observePlanned(): Flow<List<PlannedPurchaseEntity>>

    @Query(
        """
            SELECT *
            FROM purchase 
            WHERE prs_uid =:uid
        """
    )
    suspend fun getByUid(uid: String): PurchaseEntity?

    @Query(
        """
            SELECT
                prs_uid AS uid,
                prs_amount AS amount,
                prs_currency AS currency,
                trn_uid AS transactionUid,
                prs_description AS description,
                prs_updated_at AS updatedAt,
                usr_uid AS userUid,
                day_date AS date
            FROM purchase 
                INNER JOIN day ON prs_day_id = day_id
                INNER JOIN user ON prs_user_id = usr_id
                LEFT JOIN `transaction` ON prs_transaction_id = trn_id
            WHERE prs_uid = :uid 
        """
    )
    suspend fun getSyncEntity(uid: String): PurchaseSyncEntity?

    @Upsert
    suspend fun upsert(purchase: PurchaseEntity)

    @Query("DELETE FROM purchase WHERE prs_uid = :uid")
    suspend fun deleteByUid(uid: String)
}