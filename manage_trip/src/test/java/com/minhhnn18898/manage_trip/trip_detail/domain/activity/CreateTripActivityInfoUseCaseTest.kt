package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertTripActivityInfo
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
class CreateTripActivityInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var createTripActivityInfo: CreateTripActivityInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        createTripActivityInfo = CreateTripActivityInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun insertValidActivityInfo_canRetrieveActivityInfo() = runTest {
        // When
        val activityInfo = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
        )

        val result = createTripActivityInfo.execute(
            CreateTripActivityInfoUseCase.Param(
                tripId = 1L,
                activityInfo = activityInfo
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)
        val id = (result?.get(1) as Result.Success).data

        assertActivityInfoEqual(
            expected = activityInfo.copy(activityId = 1L),
            target = fakeTripDetailRepository.getActivityInfoForTesting(id)
        )
    }

    @Test
    fun insertValidActivityInfo_withInvalidId_canRetrieveActivityInfoWithAutoGeneratedId() = runTest {
        // When
        val activityInfo = TripActivityInfo(
            activityId = 10L, // use invalid hotel id when insert
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
        )

        val result = createTripActivityInfo.execute(
            CreateTripActivityInfoUseCase.Param(
                tripId = 1L,
                activityInfo = activityInfo
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)

        assertActivityInfoEqual(
            expected = activityInfo.copy(activityId = 1L),
            target = fakeTripDetailRepository.getActivityInfoForTesting(1L)
        )
    }

    @Test
    fun insertActivityInfo_throwExceptionFromRepository_canReturnError() = runTest {
        // Given
        fakeTripDetailRepository.forceError = true

        // When
        val activityInfo = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
        )

        val result = createTripActivityInfo.execute(
            CreateTripActivityInfoUseCase.Param(
                tripId = 1L,
                activityInfo = activityInfo
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionInsertTripActivityInfo::class.java)
    }
}