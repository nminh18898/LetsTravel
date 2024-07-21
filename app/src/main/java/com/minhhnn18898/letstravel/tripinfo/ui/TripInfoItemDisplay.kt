package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.annotation.DrawableRes
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo


interface TripInfoItemDisplay

data class UserTripItemDisplay(val tripId: Long, val tripName: String, @DrawableRes val defaultCoverRes: Int): TripInfoItemDisplay

data object CreateNewTripItemDisplay: TripInfoItemDisplay

fun TripInfo.toTripItemDisplay(defaultCoverResourceProvider: CoverDefaultResourceProvider): UserTripItemDisplay {
    return UserTripItemDisplay(this.tripId, this.title, defaultCoverResourceProvider.getCoverResource(this.defaultCoverId))
}