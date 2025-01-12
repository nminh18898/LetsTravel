package com.minhhnn18898.manage_trip.trip_detail.data.repo.expense

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {

    companion object {
        const val SPLITTING_MODE_EVENLY = 1
        const val SPLITTING_MODE_CUSTOM = 2
        const val SPLITTING_MODE_NO_SPLIT = 3
    }

    fun getReceiptsStream(tripId: Long): Flow<List<ReceiptWithAllPayersInfo>>

    fun getReceipt(receiptId: Long): Flow<ReceiptWithAllPayersInfo?>

    suspend fun insertReceipt(
        tripId: Long,
        receiptInfo: ReceiptInfo,
        payerInfo: List<ReceiptPayerInfo>
    ): Long

    suspend fun updateReceipt(tripId: Long, receiptModel: ReceiptModel, payerModel: ReceiptPayerModel)

    suspend fun deleteReceipt(receiptId: Long)
}