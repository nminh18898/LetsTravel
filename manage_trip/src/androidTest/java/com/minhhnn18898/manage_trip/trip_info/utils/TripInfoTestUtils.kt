package com.minhhnn18898.manage_trip.trip_info.utils

import com.google.common.truth.Truth
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel

fun assertTripInfoModelEqual(expected: TripInfoModel, target: TripInfoModel?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.title).isEqualTo(expected.title)
    Truth.assertThat(target?.coverType).isEqualTo(expected.coverType)
    Truth.assertThat(target?.defaultCoverId).isEqualTo(expected.defaultCoverId)
    Truth.assertThat(target?.customCoverPath).isEqualTo(expected.customCoverPath)
}

fun assertTripInfoEqual(expected: TripInfo, target: TripInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.title).isEqualTo(expected.title)
    Truth.assertThat(target?.coverType).isEqualTo(expected.coverType)
    Truth.assertThat(target?.defaultCoverId).isEqualTo(expected.defaultCoverId)
    Truth.assertThat(target?.customCoverPath).isEqualTo(expected.customCoverPath)
}