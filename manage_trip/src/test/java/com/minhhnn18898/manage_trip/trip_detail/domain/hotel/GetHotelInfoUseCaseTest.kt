package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertHotelInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
    var mainCoroutineRule = MainDispatcherRule()

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
        fakeTripDetailRepository.addHotelInfo(
            tripId = 1L,
            hotelInfo = hotelInfo
        )

        // When - 1: get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<HotelInfo?>>>()
        val dataResult = mutableListOf<HotelInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then - 1
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)

        assertHotelInfoEqual(
            expected = hotelInfo.copy(hotelId = 1L),
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

        fakeTripDetailRepository.updateHotelInfoForTesting(
            tripId = 1L,
            hotelInfo = hotelInfoUpdated
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
        val useCaseResult = mutableListOf<Result<Flow<HotelInfo?>>>()
        val dataResult = mutableListOf<HotelInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(dataResult[0]).isNull()
    }
    @Test
    fun getHotelInfo_throwExceptionFromRepository_returnCorrectError() = runTest {
        // Given - add valid trip info so that it can be retrieved, but throw exception from repository
        fakeTripDetailRepository.addHotelInfo(
            tripId = 1L,
            hotelInfo = HotelInfo(
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
        val useCaseResult = mutableListOf<Result<Flow<HotelInfo?>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId = 1L))?.toList(useCaseResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Error::class.java)
        val error = ((useCaseResult[1]) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }

}