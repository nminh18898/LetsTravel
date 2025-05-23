package com.minhhnn18898.manage_trip.trip_info.presentation.base

import com.minhhnn18898.manage_trip.R
import javax.inject.Inject

class TripActivityDateSeparatorResourceProvider @Inject constructor() : ITripActivityDateSeparatorResourceProvider {

    private val dateSeparatorResource = listOf(
        R.drawable.trip_activity_date_separator_leaf,
        R.drawable.trip_activity_date_separator_autumn_tree_house,
        R.drawable.trip_activity_date_separator_tree,
        R.drawable.trip_activity_date_separator_autumn_road,
        R.drawable.trip_activity_date_separator_forest,
        R.drawable.trip_activity_date_separator_autumn_city
    )

    override fun getResource(dayNumber: Int): Int {
        val ordinal = (dayNumber - 1) % dateSeparatorResource.size
        return dateSeparatorResource[ordinal]
    }
}