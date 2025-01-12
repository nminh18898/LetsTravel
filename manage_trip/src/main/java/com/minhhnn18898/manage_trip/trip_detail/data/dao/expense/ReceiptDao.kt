package com.minhhnn18898.manage_trip.trip_detail.data.dao.expense

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithPayersModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("""
        SELECT 
            r.receipt_id AS receiptId,
            r.name AS receiptName,
            r.description,
            r.price,
            r.receipt_owner AS receiptOwnerId,
            o.name AS ownerName,
            o.avatar AS ownerAvatar,
            r.created_time AS createdTime,
            r.splitting_mode AS splittingMode,
            r.trip_id AS tripId,
            p.member_id AS payerId,
            m.name AS payerName,
            m.avatar AS payerAvatar,
            p.pay_amount AS payAmount
        FROM 
            receipt r
        LEFT JOIN 
            member_info o ON r.receipt_owner = o.member_id
        LEFT JOIN 
            receipt_payer p ON r.receipt_id = p.receipt_id
        LEFT JOIN 
            member_info m ON p.member_id = m.member_id
        WHERE 
            r.trip_id = :tripId
    """)
    fun getAllReceipts(tripId: Long): Flow<List<ReceiptWithPayersModel>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReceipt(receiptModel: ReceiptModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayers(payers: List<ReceiptPayerModel>)

    @Update
    suspend fun updateReceipt(receiptModel: ReceiptModel): Int

    @Query("DELETE FROM receipt_payer WHERE receipt_id = :receiptId")
    suspend fun deleteAllPayers(receiptId: Long): Int

    @Transaction
    suspend fun insertReceiptAndPayers(receiptModel: ReceiptModel, payersInfo: List<ReceiptPayerModel>): Long {
        val receiptId = insertReceipt(receiptModel)

        if(receiptId > 0) {
            insertPayers(
                payersInfo.map {
                    ReceiptPayerModel(
                        memberId = it.memberId,
                        receiptId = receiptId,
                        payAmount = it.payAmount
                    )
                }
            )
        }

        return receiptId
    }

    @Transaction
    suspend fun updateReceiptAndPayers(receiptModel: ReceiptModel, payersInfo: List<ReceiptPayerModel>): Int {
        val receiptUpdated = updateReceipt(receiptModel)

        if(receiptUpdated > 0) {
            deleteAllPayers(receiptModel.receiptId)
            insertPayers(payersInfo)
            return 1
        }

        return 0
    }

    @Query("DELETE FROM receipt WHERE receipt_id=:receiptId")
    suspend fun deleteReceipt(receiptId: Long): Int

    @Transaction
    suspend fun deleteReceiptAndPayers(receiptId: Long): Int {
        deleteAllPayers(receiptId)
        val receiptResult = deleteReceipt(receiptId)
        return receiptResult
    }

    @Query("SELECT * FROM receipt WHERE trip_id=:tripId")
    fun getReceiptsStream(tripId: Long): Flow<List<ReceiptModel>>

    @Query("SELECT * FROM receipt WHERE trip_id=:tripId")
    fun getReceipts(tripId: Long): List<ReceiptModel>

    @Query("""
        SELECT 
            r.receipt_id AS receiptId,
            r.name AS receiptName,
            r.description,
            r.price,
            r.receipt_owner AS receiptOwnerId,
            o.name AS ownerName,
            o.avatar AS ownerAvatar,
            r.created_time AS createdTime,
            r.splitting_mode AS splittingMode,
            r.trip_id AS tripId,
            p.member_id AS payerId,
            m.name AS payerName,
            m.avatar AS payerAvatar,
            p.pay_amount AS payAmount
        FROM 
            receipt r
        LEFT JOIN 
            member_info o ON r.receipt_owner = o.member_id
        LEFT JOIN 
            receipt_payer p ON r.receipt_id = p.receipt_id
        LEFT JOIN 
            member_info m ON p.member_id = m.member_id
        WHERE 
            r.receipt_id = :receiptId
    """)
    fun getReceipt(receiptId: Long): Flow<List<ReceiptWithPayersModel>?>
}