package com.minhhnn18898.letstravel.tripdetail.domain.hotel

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfo
import com.minhhnn18898.letstravel.tripdetail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateNewHotelInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<CreateNewHotelInfoUseCase.Param, Flow<Result<Unit>>>() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertHotelInfo(params.tripId, params.hotelInfo)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(
        val tripId: Long,
        val hotelInfo: HotelInfo
    )
}