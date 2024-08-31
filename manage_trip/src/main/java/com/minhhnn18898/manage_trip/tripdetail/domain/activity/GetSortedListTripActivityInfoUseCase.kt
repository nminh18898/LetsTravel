package com.minhhnn18898.manage_trip.tripdetail.domain.activity

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.tripdetail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSortedListTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetSortedListTripActivityInfoUseCase.Param, Flow<Result<Flow<Map<Long?, List<TripActivityInfo>>>>>>() {

    override fun run(params: Param): Flow<Result<Flow<Map<Long?, List<TripActivityInfo>>>>> = flow {
        emit(Result.Loading)
        val result = repository.getSortedActivityInfo(params.tripId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val tripId: Long)
}