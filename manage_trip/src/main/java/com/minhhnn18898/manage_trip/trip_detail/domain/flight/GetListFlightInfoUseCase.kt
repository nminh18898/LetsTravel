package com.minhhnn18898.manage_trip.trip_detail.domain.flight

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListFlightInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetListFlightInfoUseCase.Param, Flow<List<FlightWithAirportInfo>>>() {

    override fun run(params: Param): Flow<List<FlightWithAirportInfo>> = repository.getListFlightInfo(params.tripId)

    data class Param(val tripId: Long)
}

