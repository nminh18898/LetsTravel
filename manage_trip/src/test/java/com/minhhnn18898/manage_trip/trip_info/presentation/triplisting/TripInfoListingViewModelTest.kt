package com.minhhnn18898.manage_trip.trip_info.presentation.triplisting

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.domain.GetListTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentState
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private val fakeTripInfoRepository = FakeTripInfoRepository()

    private lateinit var viewModel: TripInfoListingViewModel

    @Before
    fun setup() {
        viewModel = TripInfoListingViewModel(
            getListTripInfoUseCase = GetListTripInfoUseCase(fakeTripInfoRepository),
            defaultCoverResourceProvider = CoverDefaultResourceProvider()
        )
    }

    @After
    fun cleanup() {

    }

    @Test
    fun getContentState_initStateLoading() = runTest {
        val useCaseResult = mutableListOf<Result<GetSavedTripInfoContentState>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {

        }

        viewModel.contentState
    }
}