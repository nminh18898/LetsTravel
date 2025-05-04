package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.activity.DeleteTripActivityInfoUseCase
import com.minhhnn18898.test_utils.MainDispatcherRule
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo
import com.minhhnn18898.trip_data.repo.plan.ExceptionDeleteTripActivityInfo
import com.minhhnn18898.trip_data.test_helper.FakeTripDetailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteTripActivityInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var deleteTripActivityInfoUseCase: DeleteTripActivityInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        deleteTripActivityInfoUseCase = DeleteTripActivityInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun deleteValidActivity_canRetrieveNewActivityInfo() = runTest {
        // Given - add valid activity info so that it can be deleted later
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

        // When
        val result = deleteTripActivityInfoUseCase.execute(DeleteTripActivityInfoUseCase.Param(activityId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(fakeTripDetailRepository.getActivityInfoForTesting(1L)).isNull()
    }

    @Test
    fun deleteActivity_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When
        val result = deleteTripActivityInfoUseCase.execute(DeleteTripActivityInfoUseCase.Param(activityId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteTripActivityInfo::class.java)
    }

    @Test
    fun deleteValidActivity_throwExceptionFromRepository_canReturnError() = runTest {
        // Given - add valid activity info so that it can be deleted, but throw exception from repository
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
        val result = deleteTripActivityInfoUseCase.execute(DeleteTripActivityInfoUseCase.Param(activityId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteTripActivityInfo::class.java)
    }
}