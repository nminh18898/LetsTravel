package com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.flight

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.trip_data.model.plan.AirportInfo
import com.minhhnn18898.trip_data.model.plan.FlightInfo
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateFlightInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<UpdateFlightInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.updateFlightInfo(params.tripId, params.flightInfo, params.departAirport, params.destinationAirport)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(
        val tripId: Long,
        val flightInfo: FlightInfo,
        val departAirport: AirportInfo,
        val destinationAirport: AirportInfo
    )
}