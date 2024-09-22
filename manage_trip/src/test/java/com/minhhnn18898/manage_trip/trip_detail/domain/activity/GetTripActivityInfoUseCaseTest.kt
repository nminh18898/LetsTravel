package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertActivityInfoEqual
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
        val useCaseResult = mutableListOf<Result<Flow<TripActivityInfo?>>>()
        val dataResult = mutableListOf<TripActivityInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId = 1L)).toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then - 1
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)

        assertActivityInfoEqual(
            expected = activityInfo,
            target = dataResult[0]
        )

        // When - 2: update new value
        val activityInfoUpdated = TripActivityInfo(
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
        val useCaseResult = mutableListOf<Result<Flow<TripActivityInfo?>>>()
        val dataResult = mutableListOf<TripActivityInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId = 1L)).toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(dataResult[0]).isNull()
    }

    @Test
    fun getActivityInfo_throwExceptionFromRepository_returnCorrectError() = runTest {
        // Given - add valid trip info so that it can be retrieved, but throw exception from repository
        fakeTripDetailRepository.upsertActivityInfo(
            tripId = 1L,
            TripActivityInfo(
                activityId = 1L,
                title = "Discover the Delta's Charms",
                description = "Mekong Delta Tour from HCM City",
                photo = "https://testing.com/photo",
                timeFrom = 1_000_000,
                timeTo = 1_500_000,
                price = 2_000_000,
            )
        )
        fakeTripDetailRepository.forceError = true

        // When
        val useCaseResult = mutableListOf<Result<Flow<TripActivityInfo?>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId = 1L)).toList(useCaseResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Error::class.java)
        val error = ((useCaseResult[1]) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }
}