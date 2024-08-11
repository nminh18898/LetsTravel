package com.minhhnn18898.letstravel.tripinfo.usecase

import android.net.Uri
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.letstravel.tripinfo.data.model.ExceptionInsertTripInfo
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CreateTripInfoUseCase @Inject constructor(private val repository: TripInfoRepository): UseCase<CreateTripInfoUseCase.Param, Flow<Result<Unit>>>() {

    abstract class Param(open val tripName: String)

    data class DefaultCoverParam(override val tripName: String, val coverId: Int): Param(tripName)

    data class CustomCoverParam(override val tripName: String, val uri: Uri): Param(tripName)

    override fun run(params: Param):  Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.insertTripInfo(createTripInfo(params))
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

    private fun createTripInfo(param: Param): TripInfoModel {
        return when(param) {
            is DefaultCoverParam -> TripInfoModel(
                tripId = 0,
                title = param.tripName,
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = param.coverId,
                customCoverPath = ""
            )

            is CustomCoverParam -> TripInfoModel(
                tripId = 0,
                title = param.tripName,
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = param.uri.toString()
            )

            else -> throw ExceptionInsertTripInfo()
        }
    }
}