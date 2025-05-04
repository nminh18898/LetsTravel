package com.minhhnn18898.manage_trip.trip_detail.domain.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.ExceptionDeleteHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel.DeleteHotelInfoUseCase
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteHotelInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var deleteHotelInfoUseCase: DeleteHotelInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        deleteHotelInfoUseCase = DeleteHotelInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun deleteValidHotel_verifyHotelInfoNotExistInRepository() = runTest {
        // Given - add valid hotel info so that it can be deleted later
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
        val result = deleteHotelInfoUseCase.execute(DeleteHotelInfoUseCase.Param(hotelId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(fakeTripDetailRepository.getHotelInfoForTesting(1L)).isNull()
    }

    @Test
    fun deleteHotel_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.forceError = true

        // When
        val result = deleteHotelInfoUseCase.execute(DeleteHotelInfoUseCase.Param(hotelId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteHotelInfo::class.java)
    }

    @Test
    fun deleteHotel_throwExceptionFromRepository_canReturnError() = runTest {
        // Given - add valid hotel info so that it can be deleted, but throw exception from repository
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
        val result = deleteHotelInfoUseCase.execute(DeleteHotelInfoUseCase.Param(hotelId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteHotelInfo::class.java)
    }
}