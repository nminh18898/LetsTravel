package com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.flight

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlightInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetFlightInfoUseCase.Param, Flow<FlightWithAirportInfo?>>() {

    override fun run(params: Param): Flow<FlightWithAirportInfo?> = repository.getFlightInfo(params.flightId)

    data class Param(val flightId: Long)
}