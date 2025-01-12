package com.minhhnn18898.manage_trip.trip_detail.domain.receipt

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReceiptInfoUseCase @Inject constructor(private val repository: ReceiptRepository) {

    fun execute(receiptId: Long): Flow<ReceiptWithAllPayersInfo?> = repository.getReceipt(receiptId)
}