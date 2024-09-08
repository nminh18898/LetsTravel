package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateTripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertActivityInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UpdateTripActivityInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var updateTripActivityInfoUseCase: UpdateTripActivityInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        updateTripActivityInfoUseCase = UpdateTripActivityInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun updateValidActivity_canRetrieveNewActivityInfo() = runTest {
        // Given - add valid activity info so that it can be updated later
        fakeTripDetailRepository.addActivityInfo(
            tripId = 1L,
            activityInfo = TripActivityInfo(
                activityId = 1L,
                title = "Discover the Delta's Charms",
                description = "Mekong Delta Tour from HCM City",
                photo = "https://testing.com/photo",
                timeFrom = 1_000_000,
                timeTo = 1_500_000,
                price = 2_000_000,
            )
        )

        // When
        val activityInfoUpdated = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms - new",
            description = "Mekong Delta Tour from HCM City - new",
            photo = "https://testing.com/photo/new",
            timeFrom = 1_500_000,
            timeTo = 1_800_000,
            price = 2_200_000,
        )
        val result = updateTripActivityInfoUseCase.execute(
            UpdateTripActivityInfoUseCase.Param(
                tripId = 1L,
                activityInfo = activityInfoUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)

        assertActivityInfoEqual(
            expected = activityInfoUpdated,
            target = fakeTripDetailRepository.getActivityInfoForTesting(1L)
        )
    }

    @Test
    fun updateActivity_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When
        val activityInfoUpdated = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms - new",
            description = "Mekong Delta Tour from HCM City - new",
            photo = "https://testing.com/photo/new",
            timeFrom = 1_500_000,
            timeTo = 1_800_000,
            price = 2_200_000,
        )
        val result = updateTripActivityInfoUseCase.execute(
            UpdateTripActivityInfoUseCase.Param(
                tripId = 1L,
                activityInfo = activityInfoUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateTripActivityInfo::class.java)
    }

    @Test
    fun updateValidActivity_throwExceptionFromRepository_canReturnError() = runTest {
        // Given - add valid activity info so that it can be updated, but throw exception from repository
        fakeTripDetailRepository.addActivityInfo(
            tripId = 1L,
            activityInfo = TripActivityInfo(
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
        val activityInfoUpdated = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms - new",
            description = "Mekong Delta Tour from HCM City - new",
            photo = "https://testing.com/photo/new",
            timeFrom = 1_500_000,
            timeTo = 1_800_000,
            price = 2_200_000,
        )
        val result = updateTripActivityInfoUseCase.execute(
            UpdateTripActivityInfoUseCase.Param(
                tripId = 1L,
                activityInfo = activityInfoUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateTripActivityInfo::class.java)
    }
}