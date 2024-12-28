package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetHotelInfoUseCase.Param, Flow<HotelInfo?>>() {

    override fun run(params: Param): Flow<HotelInfo?> = repository.getHotelInfo(params.hotelId)

    data class Param(val hotelId: Long)
}