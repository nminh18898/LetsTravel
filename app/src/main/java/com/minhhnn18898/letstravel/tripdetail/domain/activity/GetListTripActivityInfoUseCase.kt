package com.minhhnn18898.letstravel.tripdetail.domain.activity

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.letstravel.tripdetail.data.model.TripActivityInfo
import com.minhhnn18898.letstravel.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetListTripActivityInfoUseCase.Param, Flow<Result<Flow<List<TripActivityInfo>>>>>() {

    override fun run(params: Param): Flow<Result<Flow<List<TripActivityInfo>>>> = flow {
        emit(Result.Loading)
        val result = repository.getAllActivityInfo(params.tripId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val tripId: Long)
}