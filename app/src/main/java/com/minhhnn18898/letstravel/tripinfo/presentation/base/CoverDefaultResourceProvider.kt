package com.minhhnn18898.letstravel.tripinfo.presentation.base

import androidx.annotation.DrawableRes
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.tripinfo.data.repo.DefaultCoverElement
import javax.inject.Inject

class CoverDefaultResourceProvider @Inject constructor() {

    private val defaultCoverList = mapOf(
        DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER to R.drawable.trip_cover_default_1,
        DefaultCoverElement.COVER_DEFAULT_THEME_LONG_TRIP to R.drawable.trip_cover_default_2,
        DefaultCoverElement.COVER_DEFAULT_THEME_AROUND_THE_WORLD to R.drawable.trip_cover_default_3,
        DefaultCoverElement.COVER_DEFAULT_THEME_NIGHT_DRIVE to R.drawable.trip_cover_default_4,
        DefaultCoverElement.COVER_DEFAULT_THEME_SEA to R.drawable.trip_cover_default_5,
        DefaultCoverElement.COVER_DEFAULT_THEME_NATURE to R.drawable.trip_cover_default_6
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