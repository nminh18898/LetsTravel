package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.activity.GetTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.utils.assertActivityInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo
import com.minhhnn18898.trip_data.test_helper.FakeTripDetailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTripActivityInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getTripActivityInfoUseCase: GetTripActivityInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        getTripActivityInfoUseCase = GetTripActivityInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun getExistedActivityInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add valid hotel info so that it can be retrieved later
        val activityInfo = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
        )

        fakeTripDetailRepository.upsertActivityInfo(
            tripId = 1L,
            activityInfo
        )

        // When - 1: get current data from repository
        val dataResult = mutableListOf<TripActivityInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId = 1L)).toList(dataResult)
        }

        // Then - 1
        assertActivityInfoEqual(
            expected = activityInfo,
            target = dataResult[0]
        )

        // When - 2: update new value
        val activityInfoUpdated = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms - new",
            description = "Mekong Delta Tour from HCM City - new",
            photo = "https://testing.com/photo_new",
            timeFrom = 1_300_000,
            timeTo = 1_400_000,
            price = 2_500_000,
        )

        fakeTripDetailRepository.upsertActivityInfo(
            tripId = 1L,
            activityInfoUpdated
        )

        // Then - 2
        assertActivityInfoEqual(
            expected = activityInfoUpdated,
            target = dataResult[1]
        )
    }

    @Test
    fun getNonExistedActivityInfo_returnNullValue() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When - get current data from repository
        val dataResult = mutableListOf<TripActivityInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId = 1L)).toList(dataResult)
        }

        // Then
        Truth.assertThat(dataResult[0]).isNull()
    }
}