package com.minhhnn18898.letstravel.homescreen

import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.test_helper.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.domain.GetListTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentError
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentLoading
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.UserTripDisplay
import com.minhhnn18898.test_utils.MainDispatcherRule
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel
import com.minhhnn18898.trip_data.test_helper.FakeTripInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository
    private lateinit var fakeCoverDefaultResourceProvider: FakeCoverDefaultResourceProvider

    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        fakeCoverDefaultResourceProvider = FakeCoverDefaultResourceProvider()

    }

    private fun setupViewModel() {
        viewModel = HomeScreenViewModel(
            getListTripInfoUseCase = GetListTripInfoUseCase(fakeTripInfoRepository),
            defaultCoverResourceProvider = fakeCoverDefaultResourceProvider
        )
    }

    private val tripInfoInput = mutableListOf(
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
        ),

        TripInfo(
            tripId = 3L,
            title = "Singapore",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 2,
            customCoverPath = ""
        ),
    )

    private val tripItemDisplayExpectedOutput = mutableListOf(
        UserTripDisplay(
            tripId = 1L,
            tripName = "Vietnam",
            coverDisplay = TripDefaultCoverDisplay(defaultCoverRes = 1)
        ),

        UserTripDisplay(
            tripId = 2L,
            tripName = "Thailand",
            coverDisplay = TripCustomCoverDisplay(coverPath = "https://testing.com/thailand")
        ),

        CreateNewTripCtaDisplay
    )

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun getContentState_initStateLoading() = runTest {
        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.contentState.value).isInstanceOf(GetSavedTripInfoContentLoading::class.java)
    }

    @Test
    fun getContentState_caseHasData() = runTest {
        // Given
        fakeTripInfoRepository.addTrip(*tripInfoInput.toTypedArray())
        setupViewModel()

        // When
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.contentState.collect()
        }

        // Then
        val result = viewModel.contentState.value
        Truth.assertThat(result).isInstanceOf(GetSavedTripInfoContentResult::class.java)
        Truth.assertThat((result as GetSavedTripInfoContentResult).listTripItem).isEqualTo(tripItemDisplayExpectedOutput)
    }

    @Test
    fun getContentState_caseEmptyData() = runTest {
        // Given
        fakeTripInfoRepository.reset()
        setupViewModel()

        // When
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.contentState.collect()
        }

        // Then
        val result = viewModel.contentState.value
        Truth.assertThat(result).isInstanceOf(GetSavedTripInfoContentResult::class.java)
        Truth.assertThat((result as GetSavedTripInfoContentResult).listTripItem).isEmpty()
    }

    @Test
    fun getContentState_caseError() = runTest {
        // Given
        fakeTripInfoRepository.reset()
        fakeTripInfoRepository.forceError = true
        setupViewModel()

        // When
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.contentState.collect()
        }

        // Then
        val result = viewModel.contentState.value
        Truth.assertThat(result).isInstanceOf(GetSavedTripInfoContentError::class.java)
    }
}