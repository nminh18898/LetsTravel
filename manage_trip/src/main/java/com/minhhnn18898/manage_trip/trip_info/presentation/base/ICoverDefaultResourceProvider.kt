package com.minhhnn18898.manage_trip.trip_info.presentation.base

import androidx.annotation.DrawableRes
import com.minhhnn18898.trip_data.repo.trip_info.DefaultCoverElement

interface ICoverDefaultResourceProvider {
    @DrawableRes
    fun getCoverResource(coverId: Int): Int
    fun getDefaultCoverList(): Map<DefaultCoverElement, Int>
}