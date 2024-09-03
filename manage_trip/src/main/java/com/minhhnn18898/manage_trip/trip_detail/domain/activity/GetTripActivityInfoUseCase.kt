package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetTripActivityInfoUseCase.Param, Flow<Result<Flow<TripActivityInfo?>>>>() {

    override fun run(params: Param): Flow<Result<Flow<TripActivityInfo?>>> = flow {
        emit(Result.Loading)
        val result = repository.getActivityInfo(params.activityId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val activityId: Long)
}