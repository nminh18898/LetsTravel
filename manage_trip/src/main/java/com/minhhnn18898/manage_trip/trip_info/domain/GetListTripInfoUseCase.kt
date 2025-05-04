package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.architecture.usecase.NoParamUseCase
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): NoParamUseCase<Flow<List<TripInfo>>>() {

    override fun run(): Flow<List<TripInfo>> = repository.getAllTrips()
}