package com.minhhnn18898.letstravel.tripinfo.presentation

import androidx.annotation.DrawableRes
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.repo.DefaultCoverElement

interface TripInfoItemDisplay
data class UserTripItemDisplay(val tripId: Long, val tripName: String, val coverDisplay: UserTripCoverDisplay): TripInfoItemDisplay
data object CreateNewTripItemDisplay: TripInfoItemDisplay

abstract class UserTripCoverDisplay
data class UserTripDefaultCoverDisplay(@DrawableRes val defaultCoverRes: Int): UserTripCoverDisplay()
data class UserTripCustomCoverDisplay(val coverPath: String): UserTripCoverDisplay()

fun TripInfoModel.toTripItemDisplay(defaultCoverResourceProvider: CoverDefaultResourceProvider): UserTripItemDisplay {
    val coverDisplay: UserTripCoverDisplay = when(this.coverType) {
        TripInfoModel.TRIP_COVER_TYPE_DEFAULT -> UserTripDefaultCoverDisplay(defaultCoverResourceProvider.getCoverResource(this.defaultCoverId))
        TripInfoModel.TRIP_COVER_TYPE_CUSTOM -> UserTripCustomCoverDisplay(this.customCoverPath)
        else -> UserTripDefaultCoverDisplay(defaultCoverResourceProvider.getCoverResource(DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER.type))
    }

    return UserTripItemDisplay(this.tripId, this.title, coverDisplay)
}