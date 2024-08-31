package com.minhhnn18898.tripdetail.tripinfo.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.tripdetail.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): UseCase<DeleteTripInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deleteTripInfo(params.tripId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(val tripId: Long)
}