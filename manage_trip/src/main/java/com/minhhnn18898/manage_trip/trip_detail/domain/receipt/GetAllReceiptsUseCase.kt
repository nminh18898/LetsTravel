package com.minhhnn18898.manage_trip.trip_detail.domain.receipt

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.MemberInfoRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllReceiptsUseCase @Inject constructor(private val repository: ReceiptRepository) {

    fun execute(tripId: Long): Flow<List<ReceiptWithAllPayersInfo>> = repository.getReceiptsStream(tripId)
}