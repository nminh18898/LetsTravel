package com.minhhnn18898.letstravel.tripinfo.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): ModifyTripInfoUseCase() {

    override fun run(params: Param): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertTripInfo(createTripInfoModel(params))
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}