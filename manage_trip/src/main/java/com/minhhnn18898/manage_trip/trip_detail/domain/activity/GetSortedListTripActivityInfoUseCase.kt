package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSortedListTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetSortedListTripActivityInfoUseCase.Param, Flow<Map<Long?, List<TripActivityInfo>>>>() {

    override fun run(params: Param): Flow<Map<Long?, List<TripActivityInfo>>> = repository.getSortedActivityInfo(params.tripId)

    data class Param(val tripId: Long)
}