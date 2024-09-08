package com.minhhnn18898.manage_trip.trip_info.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.model.ExceptionUpdateTripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.utils.assertTripInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateTripInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var updateTripInfoUseCase: UpdateTripInfoUseCase

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        updateTripInfoUseCase = UpdateTripInfoUseCase(fakeTripInfoRepository)
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun updateValidTripInfo_withDefaultCover_canRetrieveNewTripInfo() = runTest {
        // Given - add valid trip info so that it can be updated later
        fakeTripInfoRepository.addTripInfo(TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        ))

        // When
        val result = updateTripInfoUseCase.execute(
            ModifyTripInfoUseCase.DefaultCoverParam(
                tripId = 1L,
                tripName = "Vietnam - Sai Gon",
                coverId = 2
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
                title = "Vietnam - Sai Gon",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 2,
                customCoverPath = ""
            ),
            target = fakeTripInfoRepository.getTripInfo(id)
        )
    }

    @Test
    fun updateValidTripInfo_withCustomCover_canRetrieveNewTripInfo() = runTest {
        // Given - add valid trip info so that it can be updated later
        fakeTripInfoRepository.addTripInfo(TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        ))

        // When
        val result = updateTripInfoUseCase.execute(
            ModifyTripInfoUseCase.CustomCoverParam(
                tripId = 1L,
                tripName = "Vietnam - Sai Gon",
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
                title = "Vietnam - Sai Gon",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/something.jpg"
            ),
            target = fakeTripInfoRepository.getTripInfo(id)
        )
    }

    @Test
    fun updateTripInfo_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripInfoRepository.reset()

        // When
        val result = updateTripInfoUseCase.execute(
            ModifyTripInfoUseCase.DefaultCoverParam(
                tripId = 1L,
                tripName = "Vietnam - Sai Gon",
                coverId = 2
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateTripInfo::class.java)
    }

    @Test
    fun updateValidTripInfo_throwExceptionUpdateTripInfoFromRepository_canReturnError() = runTest {
        // Given - add valid trip info so that it can be updated, but throw exception from repository
        fakeTripInfoRepository.addTripInfo(TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        ))

        fakeTripInfoRepository.forceError = true

        // When
        val result = updateTripInfoUseCase.execute(
            ModifyTripInfoUseCase.DefaultCoverParam(
                tripId = 1L,
                tripName = "Vietnam - Sai Gon",
                coverId = 2
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateTripInfo::class.java)
    }
}