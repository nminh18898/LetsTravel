package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): ModifyTripInfoUseCase() {

    override fun run(params: ModifyTripInfoUseCase.Param): Flow<Result<Long>> = flow {
        emit(Result.Loading)
        val tripId = repository.updateTripInfo(createTripInfo(params))
        emit(Result.Success(tripId))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(val tripId: Long)
}