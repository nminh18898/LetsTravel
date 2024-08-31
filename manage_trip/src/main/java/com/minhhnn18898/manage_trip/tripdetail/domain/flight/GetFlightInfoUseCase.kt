package com.minhhnn18898.manage_trip.tripdetail.domain.flight

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFlightInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetFlightInfoUseCase.Param, Flow<Result<FlightWithAirportInfo>>>() {

    override fun run(params: Param): Flow<Result<FlightWithAirportInfo>> = flow {
        emit(Result.Loading)
        val result = repository.getFlightInfo(params.flightId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val flightId: Long)
}