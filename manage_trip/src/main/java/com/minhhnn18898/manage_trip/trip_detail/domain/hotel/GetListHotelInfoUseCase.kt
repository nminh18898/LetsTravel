package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetListHotelInfoUseCase.Param, Flow<Result<Flow<List<HotelInfo>>>>>() {

    override fun run(params: Param): Flow<Result<Flow<List<HotelInfo>>>> = flow {
        emit(Result.Loading)
        val result = repository.getAllHotelInfo(params.tripId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val tripId: Long)
}