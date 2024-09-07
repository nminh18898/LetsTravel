package com.minhhnn18898.manage_trip.trip_info.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.utils.assertTripInfoEqual
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
class GetTripInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var getTripInfoUseCase: GetTripInfoUseCase

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        getTripInfoUseCase = GetTripInfoUseCase(fakeTripInfoRepository)
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun getExistedTripInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add valid trip info so that it can be retrieved later
        fakeTripInfoRepository.addTripInfo(TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        ))

        // When - 1: get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<TripInfo?>>>()
        val dataResult = mutableListOf<TripInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then - 1
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)

        assertTripInfoEqual(
            expected = TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            ),
            target = dataResult[0]
        )

        // When - 2: update new value
        fakeTripInfoRepository.updateTripInfo(
            TripInfo(
                tripId = 1L,
                title = "Vietnam - Sai Gon",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/something.jpg"
            )
        )

        // Then - 2
        assertTripInfoEqual(
            expected = TripInfo(
                tripId = 1L,
                title = "Vietnam - Sai Gon",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/something.jpg"
            ),
            target = dataResult[1]
        )
    }

    @Test
    fun getNonExistedTripInfo_returnNullValue() = runTest {
        // Given - make sure repository has no data
        fakeTripInfoRepository.reset()

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<TripInfo?>>>()
        val dataResult = mutableListOf<TripInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId = 1L))?.toList(useCaseResult)
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
        fakeTripInfoRepository.addTripInfo(TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        ))
        fakeTripInfoRepository.forceError = true

        // When
        val useCaseResult = mutableListOf<Result<Flow<TripInfo?>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId = 1L))?.toList(useCaseResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Error::class.java)
        val error = ((useCaseResult[1]) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }
}