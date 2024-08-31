package com.minhhnn18898.manage_trip.tripinfo.presentation.base

import androidx.annotation.DrawableRes
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.tripinfo.data.repo.DefaultCoverElement
import javax.inject.Inject

class CoverDefaultResourceProvider @Inject constructor() {

    private val defaultCoverList = mapOf(
        DefaultCoverElement.COVER_DEFAULT_THEME_SPRING to R.drawable.trip_cover_default_spring,
        DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER to R.drawable.trip_cover_default_summer,
        DefaultCoverElement.COVER_DEFAULT_THEME_AUTUMN to R.drawable.trip_cover_default_autumn,
        DefaultCoverElement.COVER_DEFAULT_THEME_WINTER to R.drawable.trip_cover_default_winter,
        DefaultCoverElement.COVER_DEFAULT_THEME_BEACH to R.drawable.trip_cover_default_beach,
        DefaultCoverElement.COVER_DEFAULT_THEME_MOUNTAIN to R.drawable.trip_cover_default_mountain,
        DefaultCoverElement.COVER_DEFAULT_THEME_AURORA to R.drawable.trip_cover_default_aurora,
        DefaultCoverElement.COVER_DEFAULT_THEME_VIETNAM to R.drawable.trip_cover_default_vietnam,
        DefaultCoverElement.COVER_DEFAULT_THEME_CHINA to R.drawable.trip_cover_default_china,
        DefaultCoverElement.COVER_DEFAULT_THEME_SEA_DIVING to R.drawable.trip_cover_default_sea_diving,
    )

    private val defaultCoverListBaseValue = defaultCoverList.mapKeys { it.key.type }

    @DrawableRes
    fun getCoverResource(coverId: Int): Int {
        return defaultCoverListBaseValue[coverId] ?: 0
    }

    fun getDefaultCoverList(): Map<DefaultCoverElement, Int> {
        return defaultCoverList
    }
}