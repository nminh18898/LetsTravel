package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.architecture.usecase.NoParamUseCase
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): NoParamUseCase<Flow<List<TripInfo>>>() {

    override fun run(): Flow<List<TripInfo>> = repository.getAllTrips()
}