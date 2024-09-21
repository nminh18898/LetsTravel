package com.minhhnn18898.manage_trip.trip_info.presentation.base

import androidx.annotation.DrawableRes
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.data.repo.DefaultCoverElement

interface TripInfoItemDisplay
data class UserTripDisplay(
    val tripId: Long,
    val tripName: String,
    val coverDisplay: TripCoverDisplay
): TripInfoItemDisplay
data object CreateNewTripCtaDisplay: TripInfoItemDisplay

abstract class TripCoverDisplay
data class TripDefaultCoverDisplay(@DrawableRes val defaultCoverRes: Int): TripCoverDisplay()
data class TripCustomCoverDisplay(val coverPath: String): TripCoverDisplay()

fun TripInfo.toTripItemDisplay(defaultCoverResourceProvider: ICoverDefaultResourceProvider): UserTripDisplay {
    val coverDisplay: TripCoverDisplay = when(this.coverType) {
        TripInfoModel.TRIP_COVER_TYPE_DEFAULT -> TripDefaultCoverDisplay(defaultCoverResourceProvider.getCoverResource(this.defaultCoverId))
        TripInfoModel.TRIP_COVER_TYPE_CUSTOM -> TripCustomCoverDisplay(this.customCoverPath)
        else -> TripDefaultCoverDisplay(defaultCoverResourceProvider.getCoverResource(DefaultCoverElement.COVER_DEFAULT_THEME_BEACH.type))
    }

    return UserTripDisplay(this.tripId, this.title, coverDisplay)
}