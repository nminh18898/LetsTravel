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
class GetListTripInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var getListTripInfoUseCase: GetListTripInfoUseCase

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        getListTripInfoUseCase = GetListTripInfoUseCase(fakeTripInfoRepository)
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun getExistedTripInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add some valid trip info so that it can be retrieved later
        fakeTripInfoRepository.addTripInfo(
            TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            )
        )

        fakeTripInfoRepository.addTripInfo(
            TripInfo(
                tripId = 2L,
                title = "Thailand",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "https://testing.com/thailand"
            )
        )

        // When - 1: get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<List<TripInfo>>>>()
        val dataResult = mutableListOf<List<TripInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListTripInfoUseCase.execute()?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then - 1
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)

        assertTripInfoEqual(
            listExpected = mutableListOf(
                TripInfo(
                    tripId = 1L,
                    title = "Vietnam",
                    coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                    defaultCoverId = 1,
                    customCoverPath = ""
                ),

                TripInfo(
                    tripId = 2L,
                    title = "Thailand",
                    coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                    defaultCoverId = 0,
                    customCoverPath = "https://testing.com/thailand"
                )
            ),
            listTarget = dataResult[0]
        )

        // When - 2: update new value for the second trip
        fakeTripInfoRepository.updateTripInfo(
            TripInfo(
                tripId = 2L,
                title = "Thailand - Bangkok",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            )
        )

        // Then - 2
        assertTripInfoEqual(
            listExpected = mutableListOf(
                TripInfo(
                    tripId = 1L,
                    title = "Vietnam",
                    coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                    defaultCoverId = 1,
                    customCoverPath = ""
                ),

                TripInfo(
                    tripId = 2L,
                    title = "Thailand - Bangkok",
                    coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                    defaultCoverId = 1,
                    customCoverPath = ""
                )
            ),
            listTarget = dataResult[1]
        )
    }

    @Test
    fun getNonExistedTripInfo_returnEmptyList() = runTest {
        // Given - make sure repository has no data
        fakeTripInfoRepository.reset()

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<List<TripInfo>>>>()
        val dataResult = mutableListOf<List<TripInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListTripInfoUseCase.execute()?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(dataResult[0]).isEmpty()
    }

    @Test
    fun geTripInfo_throwExceptionFromRepository_returnCorrectError() = runTest {
        // Given - add some valid trip info so that it can be retrieved later, but throw exception from repository
        fakeTripInfoRepository.apply {
            addTripInfo(
                TripInfo(
                    tripId = 1L,
                    title = "Vietnam",
                    coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                    defaultCoverId = 1,
                    customCoverPath = ""
                )
            )
            addTripInfo(
                TripInfo(
                    tripId = 2L,
                    title = "Thailand",
                    coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                    defaultCoverId = 0,
                    customCoverPath = "https://testing.com/thailand"
                )
            )
            forceError = true
        }

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<List<TripInfo>>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListTripInfoUseCase.execute()?.toList(useCaseResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Error::class.java)
        val error = ((useCaseResult[1]) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }
}