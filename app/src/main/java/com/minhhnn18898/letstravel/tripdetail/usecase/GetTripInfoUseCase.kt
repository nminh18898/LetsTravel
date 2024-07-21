package com.minhhnn18898.letstravel.tripdetail.usecase

import com.minhhnn18898.architecture.usecase.AsyncUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTripInfoUseCase(private val repository: TripInfoRepository): AsyncUseCase<GetTripInfoUseCase.Param, Flow<Result<Flow<TripInfo>>>>() {

    override suspend fun run(params: Param): Flow<Result<Flow<TripInfo>>> = flow {
        emit(Result.Loading)
        val result = repository.getTrip(params.tripId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val tripId: Long)
}