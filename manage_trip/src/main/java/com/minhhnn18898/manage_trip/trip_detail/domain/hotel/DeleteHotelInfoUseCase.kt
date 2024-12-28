package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<DeleteHotelInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deleteHotelInfo(params.hotelId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(val hotelId: Long)
}