package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertHotelInfo
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
class CreateNewHotelInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var createHotelInfoUseCase: CreateNewHotelInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        createHotelInfoUseCase = CreateNewHotelInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun insertValidHotelInfo_canRetrieveHotelInfo() = runTest {
        // When
        val hotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )

        val result = createHotelInfoUseCase.execute(
            CreateNewHotelInfoUseCase.Param(
                tripId = 1L,
                hotelInfo = hotelInfo
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)
        val id = (result?.get(1) as Result.Success).data

        assertHotelInfoEqual(
            expected = hotelInfo.copy(hotelId = 1L),
            target = fakeTripDetailRepository.getHotelInfoForTesting(id)
        )
    }

    @Test
    fun insertValidHotelInfo_withInvalidId_canRetrieveHotelInfoWithAutoGeneratedId() = runTest {
        // When
        val hotelInfo = HotelInfo(
            hotelId = 10L, // use invalid hotel id when insert
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )

        val result = createHotelInfoUseCase.execute(
            CreateNewHotelInfoUseCase.Param(
                tripId = 1L,
                hotelInfo = hotelInfo
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)

        assertHotelInfoEqual(
            expected = hotelInfo.copy(hotelId = 1L),
            target = fakeTripDetailRepository.getHotelInfoForTesting(1L)
        )
    }

    @Test
    fun insertHotelInfo_throwExceptionFromRepository_canRetrieveHotelInfoWithAutoGeneratedId() = runTest {
        // Given
        fakeTripDetailRepository.forceError = true

        // When
        val hotelInfo = HotelInfo(
            hotelId = 10L, // use invalid hotel id
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )


        val result = createHotelInfoUseCase.execute(
            CreateNewHotelInfoUseCase.Param(
                tripId = 1L,
                hotelInfo = hotelInfo
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionInsertHotelInfo::class.java)
    }
}