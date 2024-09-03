package com.minhhnn18898.manage_trip.trip_detail.domain.flight

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateNewFlightInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<CreateNewFlightInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param):  Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertFlightInfo(params.tripId, params.flightInfo, params.departAirport, params.destinationAirport)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(
        val tripId: Long,
        val flightInfo: FlightInfo,
        val departAirport: AirportInfoModel,
        val destinationAirport: AirportInfoModel
    )
}