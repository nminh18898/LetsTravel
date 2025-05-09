package com.minhhnn18898.manage_trip.trip_info.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.test_utils.MainDispatcherRule
import com.minhhnn18898.trip_data.model.trip_info.ExceptionDeleteTripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel
import com.minhhnn18898.trip_data.test_helper.FakeTripInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteTripInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var deleteTripInfoUseCase: DeleteTripInfoUseCase

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        deleteTripInfoUseCase = DeleteTripInfoUseCase(fakeTripInfoRepository)
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun deleteValidTripInfo_withDefaultCover_dataNotExistRepositoryThen() = runTest {
        // Given - add valid trip info so that it can be deleted later
        fakeTripInfoRepository.addTrip(
            TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            )
        )

        // When
        val result = deleteTripInfoUseCase.execute(DeleteTripInfoUseCase.Param(tripId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isNull()
    }

    @Test
    fun deleteValidTripInfo_withCustomCover_dataNotExistRepositoryThen() = runTest {
        // Given - add valid trip info so that it can be deleted later
        fakeTripInfoRepository.addTrip(
            TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/something.jpg")
        )

        // When
        val result = deleteTripInfoUseCase.execute(DeleteTripInfoUseCase.Param(tripId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isNull()
    }

    @Test
    fun deleteTripInfo_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripInfoRepository.reset()

        // When
        val result = deleteTripInfoUseCase.execute(DeleteTripInfoUseCase.Param(tripId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteTripInfo::class.java)
    }

    @Test
    fun deleteValidTripInfo_throwExceptionDeleteTripInfoFromRepository_canReturnError() = runTest {
        // Given - add valid trip info so that it can be deleted, but throw exception from repository
        fakeTripInfoRepository.addTrip(
            TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        )
        )
        fakeTripInfoRepository.forceError = true

        // When
        val result = deleteTripInfoUseCase.execute(DeleteTripInfoUseCase.Param(tripId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteTripInfo::class.java)
    }
}