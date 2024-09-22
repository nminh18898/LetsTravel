package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertHotelInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UpdateHotelInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var updateHotelInfoUseCase: UpdateHotelInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        updateHotelInfoUseCase = UpdateHotelInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun updateValidHotel_canRetrieveNewHotelInfo() = runTest {
        // Given - add valid hotel info so that it can be updated later
        fakeTripDetailRepository.upsertHotelInfo(
            tripId = 1L,
             HotelInfo(
                hotelId = 1L,
                hotelName = "Liberty Central Riverside Hotel",
                address = "District 1, Ho Chi Minh City",
                checkInDate = 1_000_000,
                checkOutDate = 1_200_000,
                price = 2_200_000
            )
        )

        // When
        val hotelInfoUpdated = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel - new",
            address = "District 1, Ho Chi Minh City - new",
            checkInDate = 1_200_000,
            checkOutDate = 1_500_000,
            price = 2_500_000
        )
        val result = updateHotelInfoUseCase.execute(
            UpdateHotelInfoUseCase.Param(
                tripId = 1L,
                hotelInfo = hotelInfoUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)

        assertHotelInfoEqual(
            expected = hotelInfoUpdated,
            target = fakeTripDetailRepository.getHotelInfoForTesting(1L)
        )
    }

    @Test
    fun updateHotel_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When
        val hotelInfoUpdated = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel - new",
            address = "District 1, Ho Chi Minh City - new",
            checkInDate = 1_200_000,
            checkOutDate = 1_500_000,
            price = 2_500_000
        )
        val result = updateHotelInfoUseCase.execute(
            UpdateHotelInfoUseCase.Param(
                tripId = 1L,
                hotelInfo = hotelInfoUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateHotelInfo::class.java)
    }

    @Test
    fun updateValidHotel_throwExceptionFromRepository_canReturnError() = runTest {
        // Given - add valid hotel info so that it can be updated, but throw exception from repository
        fakeTripDetailRepository.upsertHotelInfo(
            tripId = 1L,
            HotelInfo(
                hotelId = 1L,
                hotelName = "Liberty Central Riverside Hotel",
                address = "District 1, Ho Chi Minh City",
                checkInDate = 1_000_000,
                checkOutDate = 1_200_000,
                price = 2_200_000
            )
        )
        fakeTripDetailRepository.forceError = true

        // When
        val hotelInfoUpdated = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel - new",
            address = "District 1, Ho Chi Minh City - new",
            checkInDate = 1_200_000,
            checkOutDate = 1_500_000,
            price = 2_500_000
        )
        val result = updateHotelInfoUseCase.execute(
            UpdateHotelInfoUseCase.Param(
                tripId = 1L,
                hotelInfo = hotelInfoUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateHotelInfo::class.java)
    }
}