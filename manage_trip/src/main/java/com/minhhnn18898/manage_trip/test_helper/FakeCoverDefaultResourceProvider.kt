package com.minhhnn18898.manage_trip.test_helper

import com.minhhnn18898.manage_trip.trip_info.data.repo.DefaultCoverElement
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ICoverDefaultResourceProvider

class FakeCoverDefaultResourceProvider: ICoverDefaultResourceProvider {
    override fun getCoverResource(coverId: Int): Int {
        return coverId
    }

    override fun getDefaultCoverList(): Map<DefaultCoverElement, Int> {
        return mapOf(
             DefaultCoverElement.COVER_DEFAULT_THEME_SPRING to 1,
             DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER to 2,
             DefaultCoverElement.COVER_DEFAULT_THEME_AUTUMN to 3,
             DefaultCoverElement.COVER_DEFAULT_THEME_WINTER to 4,
             DefaultCoverElement.COVER_DEFAULT_THEME_BEACH to 5,
             DefaultCoverElement.COVER_DEFAULT_THEME_MOUNTAIN to 6,
             DefaultCoverElement.COVER_DEFAULT_THEME_AURORA to 7,
             DefaultCoverElement.COVER_DEFAULT_THEME_VIETNAM to 8,
             DefaultCoverElement.COVER_DEFAULT_THEME_CHINA to 9,
             DefaultCoverElement.COVER_DEFAULT_THEME_SEA_DIVING to 10
        )
    }
}