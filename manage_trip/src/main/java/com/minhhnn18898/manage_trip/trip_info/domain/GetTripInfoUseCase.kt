package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): UseCase<GetTripInfoUseCase.Param, Flow<TripInfo?>>() {

    override fun run(params: Param): Flow<TripInfo?> = repository.getTrip(params.tripId)

    data class Param(val tripId: Long)
}