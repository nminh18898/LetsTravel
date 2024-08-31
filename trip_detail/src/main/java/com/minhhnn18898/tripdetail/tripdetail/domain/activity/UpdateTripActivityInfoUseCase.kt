package com.minhhnn18898.tripdetail.tripdetail.domain.activity

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.tripdetail.tripdetail.data.model.TripActivityInfo
import com.minhhnn18898.tripdetail.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<UpdateTripActivityInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.updateActivityInfo(params.tripId, params.activityInfo)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(
        val tripId: Long,
        val activityInfo: TripActivityInfo
    )
}