package com.minhhnn18898.letstravel.tripinfo.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): ModifyTripInfoUseCase() {

    override fun run(params: ModifyTripInfoUseCase.Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.updateTripInfo(createTripInfoModel(params))
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    class Param(val tripId: Long)
}