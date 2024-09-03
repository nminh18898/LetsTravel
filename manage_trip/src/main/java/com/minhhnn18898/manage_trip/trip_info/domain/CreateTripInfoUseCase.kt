package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): ModifyTripInfoUseCase() {

    override fun run(params: Param): Flow<Result<Long>> = flow {
        emit(Result.Loading)
        val tripId = repository.insertTripInfo(createTripInfo(params))
        emit(Result.Success(tripId))
    }.catch {
        emit(Result.Error(it))
    }
}