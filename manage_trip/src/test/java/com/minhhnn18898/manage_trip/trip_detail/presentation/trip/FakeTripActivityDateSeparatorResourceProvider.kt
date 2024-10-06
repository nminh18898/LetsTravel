package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import com.minhhnn18898.manage_trip.trip_info.presentation.base.ITripActivityDateSeparatorResourceProvider

class FakeTripActivityDateSeparatorResourceProvider: ITripActivityDateSeparatorResourceProvider {

    override fun getResource(dayNumber: Int): Int {
        return dayNumber
    }
}