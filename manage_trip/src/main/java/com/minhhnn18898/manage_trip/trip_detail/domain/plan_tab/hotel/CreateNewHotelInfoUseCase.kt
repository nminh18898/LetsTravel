package com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.trip_data.model.plan.HotelInfo
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateNewHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<CreateNewHotelInfoUseCase.Param, Flow<Result<Long>>>() {

    override fun run(params: Param): Flow<Result<Long>> = flow {
        emit(Result.Loading)
        val result = repository.insertHotelInfo(params.tripId, params.hotelInfo)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(
        val tripId: Long,
        val hotelInfo: HotelInfo
    )
}