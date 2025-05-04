package com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.trip_data.model.plan.HotelInfo
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetListHotelInfoUseCase.Param, Flow<List<HotelInfo>>>() {

    override fun run(params: Param): Flow<List<HotelInfo>> = repository.getAllHotelInfo(params.tripId)

    data class Param(val tripId: Long)
}