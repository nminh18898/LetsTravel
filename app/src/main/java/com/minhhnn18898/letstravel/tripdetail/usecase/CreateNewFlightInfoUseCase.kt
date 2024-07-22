package com.minhhnn18898.letstravel.tripdetail.usecase

import com.minhhnn18898.architecture.usecase.AsyncUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfo
import com.minhhnn18898.letstravel.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateNewFlightInfoUseCase(private val repository: TripDetailRepository): AsyncUseCase<CreateNewFlightInfoUseCase.Param, Flow<Result<Unit>>>() {

    class Param(
        val tripId: Long,
        val flightInfo: FlightInfo,
        val departAirport: AirportInfo,
        val destinationAirport: AirportInfo
    )

    override suspend fun run(params: Param):  Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertFlightInfo(params.tripId, params.flightInfo, params.departAirport, params.destinationAirport)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}