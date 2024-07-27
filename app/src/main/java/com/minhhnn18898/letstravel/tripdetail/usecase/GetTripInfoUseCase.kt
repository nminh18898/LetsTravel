package com.minhhnn18898.letstravel.tripdetail.usecase

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): UseCase<GetTripInfoUseCase.Param, Flow<Result<Flow<TripInfo>>>>() {

    override fun run(params: Param): Flow<Result<Flow<TripInfo>>> = flow {
        emit(Result.Loading)
        val result = repository.getTrip(params.tripId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val tripId: Long)
}