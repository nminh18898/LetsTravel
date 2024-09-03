package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<CreateTripActivityInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertActivityInfo(params.tripId, params.activityInfo)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(
        val tripId: Long,
        val activityInfo: TripActivityInfo
    )
}