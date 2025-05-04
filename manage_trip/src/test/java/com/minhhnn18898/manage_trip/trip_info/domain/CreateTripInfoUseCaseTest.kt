package com.minhhnn18898.manage_trip.trip_info.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_info.utils.assertTripInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import com.minhhnn18898.trip_data.model.trip_info.ExceptionInsertTripInfo
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

@ExperimentalCoroutinesApi
class CreateTripInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var createTripInfoUseCase: CreateTripInfoUseCase

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        createTripInfoUseCase = CreateTripInfoUseCase(fakeTripInfoRepository)
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun insertValidTripInfo_withDefaultCover_withValidId_canRetrieveTheSameTripInfo() = runTest {
        // When
        val result = createTripInfoUseCase.execute(
            ModifyTripInfoUseCase.DefaultCoverParam(
                tripId = 0L,
                tripName = "Vietnam",
                coverId = 1
            )
        ).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        val id = (result[1] as Result.Success).data

        assertTripInfoEqual(
            expected = TripInfo(
                tripId = id,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            ),
            target = fakeTripInfoRepository.getTripInfo(id)
        )
    }

    @Test
    fun insertValidTripInfo_withCustomCover_canRetrieveTheSameTripInfo() = runTest {
        // When
        val result = createTripInfoUseCase.execute(
            ModifyTripInfoUseCase.CustomCoverParam(
                tripId = 0L,
                tripName = "Vietnam",
                uri = "/sdcard/something.jpg"
            )
        ).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        val id = (result[1] as Result.Success).data

        assertTripInfoEqual(
            expected = TripInfo(
                tripId = id,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/something.jpg"
            ),
            target = fakeTripInfoRepository.getTripInfo(id)
        )
    }

    @Test
    fun insertValidTripInfo_throwExceptionInsertFromRepository_canReturnCorrectError() = runTest {
        // Given
        fakeTripInfoRepository.forceError = true

        // When
        val result = createTripInfoUseCase.execute(
            ModifyTripInfoUseCase.DefaultCoverParam(
                tripId = 0L,
                tripName = "Vietnam",
                coverId = 1
            )
        ).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionInsertTripInfo::class.java)
    }
}