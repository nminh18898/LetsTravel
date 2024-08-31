package com.minhhnn18898.manage_trip.tripdetail.domain.hotel

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.tripdetail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetHotelInfoUseCase.Param, Flow<Result<HotelInfo>>>() {

    override fun run(params: Param): Flow<Result<HotelInfo>> = flow {
        emit(Result.Loading)
        val result = repository.getHotelInfo(params.hotelId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val hotelId: Long)
}