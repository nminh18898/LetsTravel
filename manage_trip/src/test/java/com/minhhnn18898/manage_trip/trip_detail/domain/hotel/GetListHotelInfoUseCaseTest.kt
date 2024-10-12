package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.test_helper.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
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

@Suppress("SpellCheckingInspection")
@ExperimentalCoroutinesApi
class GetListHotelInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getListHotelInfoUseCase: GetListHotelInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        getListHotelInfoUseCase = GetListHotelInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun getExistedTripInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add some valid trip info so that it can be retrieved later
        val firstHotel = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )

        val secondHotel = HotelInfo(
            hotelId = 2L,
            hotelName =  "Eastin Grand Hotel",
            address =  "Phu Nhuan District, Ho Chi Minh City",
            price = 3_000_000,
            checkInDate =  2_000_000,
            checkOutDate = 2_200_000
        )

        fakeTripDetailRepository.upsertHotelInfo(
            tripId = 1L,
            firstHotel
        )

        fakeTripDetailRepository.upsertHotelInfo(
            tripId = 1L,
            secondHotel
        )

        // When - 1: get current data from repository
        val dataResult = mutableListOf<List<HotelInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListHotelInfoUseCase.execute(GetListHotelInfoUseCase.Param(tripId = 1L)).toList(dataResult)
        }

        // Then - 1
        assertHotelInfoEqual(
            listExpected = mutableListOf(
                firstHotel,
                secondHotel
            ),
            listTarget = dataResult[0]
        )

        // When - 2: update new value for the second trip
        val hotelInfoUpdated = HotelInfo(
            hotelId = 2L,
            hotelName =  "Eastin Grand Hotel - new",
            address =  "Phu Nhuan District, Ho Chi Minh City - new",
            price = 3_300_000,
            checkInDate =  2_200_000,
            checkOutDate = 2_400_000
        )
        fakeTripDetailRepository.upsertHotelInfo(tripId = 1L, hotelInfoUpdated)

        // Then - 2
        assertHotelInfoEqual(
            listExpected = mutableListOf(
                firstHotel,
                hotelInfoUpdated
            ),
            listTarget = dataResult[1]
        )
    }

    @Test
    fun getNonExistedHotelInfo_returnEmptyList() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When - get current data from repository
        val dataResult = mutableListOf<List<HotelInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListHotelInfoUseCase.execute(GetListHotelInfoUseCase.Param(tripId = 1L)).toList(dataResult)
        }

        // Then
        Truth.assertThat(dataResult[0]).isEmpty()
    }
}