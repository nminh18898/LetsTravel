package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.test_helper.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel.GetHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.utils.assertHotelInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GetHotelInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getHotelInfoUseCase: GetHotelInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        getHotelInfoUseCase = GetHotelInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun getExistedHotelInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add valid hotel info so that it can be retrieved later
        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(
            tripId = 1L,
            hotelInfo
        )

        // When - 1: get current data from repository
        val dataResult = mutableListOf<HotelInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId = 1L)).toList(dataResult)
        }

        // Then - 1
        assertHotelInfoEqual(
            expected = hotelInfo,
            target = dataResult[0]
        )

        // When - 2: update new value
        val hotelInfoUpdated = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel - new",
            address = "District 1, Ho Chi Minh City - new",
            checkInDate = 1_200_000,
            checkOutDate = 1_500_000,
            price = 2_500_000
        )

        fakeTripDetailRepository.upsertHotelInfo(
            tripId = 1L,
            hotelInfoUpdated
        )

        // Then - 2
        assertHotelInfoEqual(
            expected = hotelInfoUpdated,
            target = dataResult[1]
        )
    }

    @Test
    fun getNonExistedHotelInfo_returnNullValue() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When - get current data from repository
        val dataResult = mutableListOf<HotelInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId = 1L)).toList(dataResult)
        }

        // Then
        Truth.assertThat(dataResult[0]).isNull()
    }
}