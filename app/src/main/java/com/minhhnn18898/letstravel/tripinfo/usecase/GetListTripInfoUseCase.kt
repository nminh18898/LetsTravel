package com.minhhnn18898.letstravel.tripinfo.usecase

import com.minhhnn18898.architecture.usecase.AsyncUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetListTripInfoUseCase(private val repository: TripInfoRepository): AsyncUseCase<Unit, Flow<Result<List<TripInfo>>>>() {

    override suspend fun run(params: Unit): Flow<Result<List<TripInfo>>> = flow {
        emit(Result.Loading)
        val result = repository.getAllTrips()
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

}