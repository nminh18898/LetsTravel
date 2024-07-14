package com.minhhnn18898.letstravel.tripinfo.data.repo

import com.minhhnn18898.letstravel.tripinfo.data.dao.TripInfoDao

class TripInfoRepository(private val tripListingDao: TripInfoDao) {


    private val defaultCoverIdList = listOf(
        DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER,
        DefaultCoverElement.COVER_DEFAULT_THEME_LONG_TRIP,
        DefaultCoverElement.COVER_DEFAULT_THEME_AROUND_THE_WORLD,
        DefaultCoverElement.COVER_DEFAULT_THEME_NIGHT_DRIVE,
        DefaultCoverElement.COVER_DEFAULT_THEME_SEA,
        DefaultCoverElement.COVER_DEFAULT_THEME_NATURE
    )

    fun getAllTrips() {

    }

    fun getListDefaultCoverElements(): List<DefaultCoverElement> = defaultCoverIdList
}