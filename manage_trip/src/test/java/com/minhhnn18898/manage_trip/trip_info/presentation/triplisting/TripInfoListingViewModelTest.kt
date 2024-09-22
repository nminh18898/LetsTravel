package com.minhhnn18898.manage_trip.trip_info.presentation.triplisting

import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.trip_info.data.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.domain.GetListTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentLoading
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.UserTripDisplay
import com.minhhnn18898.test_utils.MainDispatcherRule
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
class TripInfoListingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository
    private lateinit var fakeCoverDefaultResourceProvider: FakeCoverDefaultResourceProvider

    private lateinit var viewModel: TripInfoListingViewModel

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        fakeCoverDefaultResourceProvider = FakeCoverDefaultResourceProvider()
        viewModel = TripInfoListingViewModel(
            getListTripInfoUseCase = GetListTripInfoUseCase(fakeTripInfoRepository),
            defaultCoverResourceProvider = fakeCoverDefaultResourceProvider
        )
    }

    @After
    fun cleanup() {

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
        CreateNewTripCtaDisplay,

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

        UserTripDisplay(
            tripId = 3L,
            tripName = "Singapore",
            coverDisplay = TripDefaultCoverDisplay(defaultCoverRes = 2)
        )
    )

    @Test
    fun getContentState_initStateLoading() = runTest {
        Truth.assertThat(viewModel.contentState.value).isInstanceOf(GetSavedTripInfoContentLoading::class.java)
    }

    @Test
    fun getContentState_caseHasData() = runTest {
        // Given
        fakeTripInfoRepository.addTrip(*tripInfoInput.toTypedArray())

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

        // When
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.contentState.collect()
        }

        // Then
        val result = viewModel.contentState.value
        Truth.assertThat(result).isInstanceOf(GetSavedTripInfoContentResult::class.java)
        Truth.assertThat((result as GetSavedTripInfoContentResult).listTripItem).isEmpty()
    }

}