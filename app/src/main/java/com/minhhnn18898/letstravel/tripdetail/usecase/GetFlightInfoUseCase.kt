package com.minhhnn18898.letstravel.tripdetail.usecase

import com.minhhnn18898.architecture.usecase.AsyncUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFlightInfoUseCase @Inject constructor(private val repository: TripDetailRepository): AsyncUseCase<GetFlightInfoUseCase.Param, Flow<Result<Flow<List<FlightWithAirportInfo>>>>>() {

    data class Param(val tripId: Long)

    override suspend fun run(params: Param): Flow<Result<Flow<List<FlightWithAirportInfo>>>> = flow {
        emit(Result.Loading)
        val result = repository.getFlightInfo(params.tripId)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }
}

