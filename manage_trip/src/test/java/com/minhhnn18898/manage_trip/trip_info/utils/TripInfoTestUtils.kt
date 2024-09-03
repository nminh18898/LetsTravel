package com.minhhnn18898.manage_trip.trip_info.utils

import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo

fun assertTripInfoEqual(expected: TripInfo, target: TripInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.title).isEqualTo(expected.title)
    Truth.assertThat(target?.coverType).isEqualTo(expected.coverType)
    Truth.assertThat(target?.defaultCoverId).isEqualTo(expected.defaultCoverId)
    Truth.assertThat(target?.customCoverPath).isEqualTo(expected.customCoverPath)
}

fun assertTripInfoEqual(listExpected: List<TripInfo>, listTarget: List<TripInfo?>) {
    Truth.assertThat(listExpected).hasSize(listTarget.size)

    for(i in listExpected.indices) {
        val expected = listExpected[i]
        val target = listTarget[i]

        assertTripInfoEqual(expected = expected, target = target)
    }
}