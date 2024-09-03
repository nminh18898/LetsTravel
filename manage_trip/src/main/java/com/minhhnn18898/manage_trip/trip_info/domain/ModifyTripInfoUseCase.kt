package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import kotlinx.coroutines.flow.Flow

abstract class ModifyTripInfoUseCase: UseCase<ModifyTripInfoUseCase.Param, Flow<Result<Long>>>() {

    abstract class Param(open val tripId: Long, open val tripName: String)

    data class DefaultCoverParam(override val tripId: Long, override val tripName: String, val coverId: Int): Param(tripId, tripName)

    data class CustomCoverParam(override val tripId: Long, override val tripName: String, val uri: String): Param(tripId, tripName)

    protected fun createTripInfo(param: Param): TripInfo {
        return when(param) {
            is DefaultCoverParam -> TripInfo(
                tripId = param.tripId,
                title = param.tripName,
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = param.coverId,
                customCoverPath = ""
            )

            is CustomCoverParam -> TripInfo(
                tripId = param.tripId,
                title = param.tripName,
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = param.uri
            )

            else -> throw Exception()
        }
    }
}