package com.minhhnn18898.letstravel.tripinfo.usecase

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): UseCase<Unit, Flow<Result<Flow<List<TripInfoModel>>>>>() {

    override fun run(params: Unit): Flow<Result<Flow<List<TripInfoModel>>>> = flow {
        emit(Result.Loading)
        val result = repository.getAllTrips()
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

}