package com.minhhnn18898.letstravel.tripinfo.usecase

import com.minhhnn18898.architecture.usecase.AsyncUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CreateTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): AsyncUseCase<CreateTripInfoUseCase.Param, Flow<Result<Unit>>>() {

    data class Param(val tripName: String, val coverId: Int)

    override suspend fun run(params: Param):  Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertTripInfo(TripInfo(0, params.tripName, params.coverId))
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}