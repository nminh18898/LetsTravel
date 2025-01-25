package com.minhhnn18898.manage_trip.trip_detail.data.repo.expense

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.ReceiptDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerInfo
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
        return receiptDao
            .getAllReceipts(tripId)
            .map {
                it.toReceiptWithAllPayersInfo()
            }
    }

    override fun getReceipt(receiptId: Long): Flow<ReceiptWithAllPayersInfo?> {
        return receiptDao
            .getReceipt(receiptId)
            .map {
                it?.toReceiptWithAllPayersInfo()?.firstOrNull()
            }
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

    override suspend fun updateReceipt(tripId: Long, receiptInfo: ReceiptInfo, payerInfo: List<ReceiptPayerInfo>) = withContext(ioDispatcher) {
        val receiptId = receiptInfo.receiptId

        val result = receiptDao.updateReceiptAndPayers(
            receiptModel = receiptInfo
                .toReceiptModel(tripId),
            payersInfo = payerInfo
                .map {
                    it.toReceiptPayerModel(receiptId)
                }
        )

        if (result <= 0) {
            throw ExceptionUpdateReceipt()
        }
    }

    override suspend fun deleteReceipt(receiptId: Long) = withContext(ioDispatcher) {
        val result = receiptDao.deleteReceipt(receiptId)

        if(result <= 0) {
            throw ExceptionDeleteReceipt()
        }
    }
}