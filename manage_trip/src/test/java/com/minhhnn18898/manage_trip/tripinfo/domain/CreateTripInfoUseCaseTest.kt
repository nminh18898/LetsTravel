package com.minhhnn18898.manage_trip.tripinfo.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.tripinfo.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionInsertTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.tripinfo.utils.assertTripInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
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
    var mainCoroutineRule = MainDispatcherRule()

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
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)
        val id = (result?.get(1) as Result.Success).data

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
    fun insertValidTripInfo_withDefaultCover_withInvalidId_canRetrieveTheSameTripInfo() = runTest {
        // When
        val result = createTripInfoUseCase.execute(
            ModifyTripInfoUseCase.DefaultCoverParam(
                tripId = 10L, // use an id that different then 0, but we still a valid auto generated id is return
                tripName = "Vietnam",
                coverId = 1
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)
        val id = (result?.get(1) as Result.Success).data

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
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)
        val id = (result?.get(1) as Result.Success).data

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
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionInsertTripInfo::class.java)
    }
}