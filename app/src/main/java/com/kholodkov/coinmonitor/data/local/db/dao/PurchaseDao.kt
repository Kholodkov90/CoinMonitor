package com.kholodkov.coinmonitor.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kholodkov.coinmonitor.data.local.db.entity.purchase.FullPurchaseEntity
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
                prs_amount AS amount,
                prs_currency AS currency,
                prs_description AS description,
                usr_display_name AS userName
            FROM purchase 
                INNER JOIN day ON prs_day_id = day_id
                INNER JOIN user ON prs_user_id = usr_id
        """
    )
    fun observeAll(): Flow<List<FullPurchaseEntity>>

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
                prs_description AS description,
                prs_updated_at AS updatedAt,
                usr_uid AS userUid,
                day_date AS date
            FROM purchase 
                INNER JOIN day ON prs_day_id = day_id
                INNER JOIN user ON prs_user_id = usr_id
            WHERE prs_uid = :uid 
        """
    )
    suspend fun getSyncEntity(uid: String): PurchaseSyncEntity?

    @Upsert
    suspend fun upsert(purchase: PurchaseEntity)

    @Query("DELETE FROM purchase WHERE prs_uid = :uid")
    suspend fun deleteByUid(uid: String)
}