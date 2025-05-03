package com.minhhnn18898.trip_data.repo.expense

import com.minhhnn18898.trip_data.model.expense.ReceiptInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptWithAllPayersInfo
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

    suspend fun updateReceipt(
        tripId: Long,
        receiptInfo: ReceiptInfo,
        payerInfo: List<ReceiptPayerInfo>
    )

    suspend fun deleteReceipt(receiptId: Long)
}