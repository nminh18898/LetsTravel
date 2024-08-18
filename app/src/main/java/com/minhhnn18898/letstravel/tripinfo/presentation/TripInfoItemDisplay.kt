package com.minhhnn18898.letstravel.tripinfo.presentation

import androidx.annotation.DrawableRes
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.repo.DefaultCoverElement

interface TripInfoItemDisplay
data class TripItemDisplay(val tripId: Long, val tripName: String, val coverDisplay: TripCoverDisplay): TripInfoItemDisplay
data object CreateNewTripItemDisplay: TripInfoItemDisplay

abstract class TripCoverDisplay
data class TripDefaultCoverDisplay(@DrawableRes val defaultCoverRes: Int): TripCoverDisplay()
data class TripCustomCoverDisplay(val coverPath: String): TripCoverDisplay()

fun TripInfo.toTripItemDisplay(defaultCoverResourceProvider: CoverDefaultResourceProvider): TripItemDisplay {
    val coverDisplay: TripCoverDisplay = when(this.coverType) {
        TripInfoModel.TRIP_COVER_TYPE_DEFAULT -> TripDefaultCoverDisplay(defaultCoverResourceProvider.getCoverResource(this.defaultCoverId))
        TripInfoModel.TRIP_COVER_TYPE_CUSTOM -> TripCustomCoverDisplay(this.customCoverPath)
        else -> TripDefaultCoverDisplay(defaultCoverResourceProvider.getCoverResource(DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER.type))
    }

    return TripItemDisplay(this.tripId, this.title, coverDisplay)
}