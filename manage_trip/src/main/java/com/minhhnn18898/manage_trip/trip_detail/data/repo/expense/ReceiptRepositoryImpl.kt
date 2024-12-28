package com.minhhnn18898.manage_trip.trip_detail.data.repo.expense

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.ReceiptDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.toReceiptModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.toReceiptPayerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.toReceiptWithAllPayersInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ReceiptRepositoryImpl(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val receiptDao: ReceiptDao
): ReceiptRepository {

    override fun getReceiptsStream(tripId: Long): Flow<List<ReceiptWithAllPayersInfo>> {
        return receiptDao.getAllReceipts(tripId).map { it.toReceiptWithAllPayersInfo() }
    }

    override fun getReceipts(tripId: Long): List<ReceiptWithAllPayersInfo> {
        return emptyList()
    }

    override suspend fun getReceipt(receiptId: Long): ReceiptWithAllPayersInfo? {
        return null
    }

    override suspend fun insertReceipt(
        tripId: Long,
        receiptInfo: ReceiptInfo,
        payerInfo: List<ReceiptPayerInfo>
    ): Long = withContext(ioDispatcher) {
        val result = receiptDao.insertReceiptAndPayers(
            receiptModel = receiptInfo
                .toReceiptModel(tripId)
                .copy(receiptId = 0L),
            payersInfo = payerInfo
                .map {
                    it.toReceiptPayerModel(receiptId = 0L)
                }
        )

        if (result == -1L) {
            throw ExceptionInsertReceipt()
        }

        result
    }

    override suspend fun updateReceipt(tripId: Long, receiptModel: ReceiptModel, payerModel: ReceiptPayerModel) {

    }

    override suspend fun deleteReceipt(receiptId: Long) {

    }
}