package com.minhhnn18898.manage_trip.tripdetail.domain.activity

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<DeleteTripActivityInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deleteActivityInfo(params.activityId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(val activityId: Long)
}