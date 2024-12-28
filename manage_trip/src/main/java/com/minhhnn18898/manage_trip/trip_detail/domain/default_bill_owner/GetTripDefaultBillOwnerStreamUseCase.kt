package com.minhhnn18898.manage_trip.trip_detail.domain.default_bill_owner

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.DefaultBillOwnerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripDefaultBillOwnerStreamUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(tripId: Long): Flow<DefaultBillOwnerInfo?> = repository.getDefaultBillOwnerStream(tripId)
}