package com.minhhnn18898.trip_data.test_helper

import com.minhhnn18898.trip_data.model.expense.ReceiptInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.trip_data.repo.expense.ReceiptRepository
import kotlinx.coroutines.flow.Flow

class FakeReceiptRepository: ReceiptRepository {
    override fun getReceiptsStream(tripId: Long): Flow<List<ReceiptWithAllPayersInfo>> {
        TODO("Not yet implemented")
    }

    override fun getReceipt(receiptId: Long): Flow<ReceiptWithAllPayersInfo?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertReceipt(tripId: Long, receiptInfo: ReceiptInfo, payerInfo: List<ReceiptPayerInfo>): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateReceipt(tripId: Long, receiptInfo: ReceiptInfo, payerInfo: List<ReceiptPayerInfo>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReceipt(receiptId: Long) {
        TODO("Not yet implemented")
    }
}