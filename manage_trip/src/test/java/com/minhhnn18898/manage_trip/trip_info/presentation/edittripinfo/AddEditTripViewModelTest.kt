package com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.manage_trip.trip_info.data.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.domain.CreateTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.DeleteTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.GetListDefaultCoverUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.GetTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.UpdateTripInfoUseCase
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTripViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository
    private lateinit var fakeCoverDefaultResourceProvider: FakeCoverDefaultResourceProvider

    private lateinit var viewModel: AddEditTripViewModel

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        fakeCoverDefaultResourceProvider = FakeCoverDefaultResourceProvider()
    }

    private fun setupViewModel() {
        viewModel = AddEditTripViewModel(
            defaultCoverResourceProvider = fakeCoverDefaultResourceProvider,
            getListDefaultCoverUseCase = GetListDefaultCoverUseCase(fakeTripInfoRepository),
            createTripInfoUseCase = CreateTripInfoUseCase(fakeTripInfoRepository),
            getTripInfoUseCase = GetTripInfoUseCase(fakeTripInfoRepository),
            updateTripInfoUseCase = UpdateTripInfoUseCase(fakeTripInfoRepository),
            deleteTripInfoUseCase = DeleteTripInfoUseCase(fakeTripInfoRepository),
            savedStateHandle = SavedStateHandle(mapOf(MainAppRoute.tripIdArg to 1L))
        )
    }

    @After
    fun cleanup() {

    }

    @Test
    fun loadTripInfo_loading() = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.uiState.value.isLoading).isTrue()

        // When: execute pending coroutines actions
        advanceUntilIdle()

        // Then: progress indicator is hidden
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun loadTripInfo_tripHasDefaultCover_tripShown() = runTest {
        // Given
        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        )

        // When
        setupViewModel()
        fakeTripInfoRepository.addTrip(tripInfo)

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.tripTitle).isEqualTo(tripInfo.title)
        Truth.assertThat(uiState.listCoverItems).isEqualTo(getListDefaultCoverOutput(selectedId = 1))
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isFalse()
        Truth.assertThat(uiState.canDeleteTrip).isTrue()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.newCreatedTripUiState).isNull()
        Truth.assertThat(uiState.isTripUpdated).isFalse()
        Truth.assertThat(uiState.isTripDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isTrue()
    }

    @Test
    fun getUiState() {
    }

    @Test
    fun onTripTitleUpdated() {
    }

    @Test
    fun onCoverSelected() {
    }

    @Test
    fun onSaveClick() {
    }

    @Test
    fun onNewPhotoPicked() {
    }

    @Test
    fun onDeleteClick() {
    }

    @Test
    fun onDeleteConfirm() {
    }

    @Test
    fun onDeleteDismiss() {
    }

    private fun getListDefaultCoverOutput(selectedId: Int): List<AddEditTripViewModel.CoverUIElement> {
        return listDefaultCoverOutput.map { it.copy(isSelected = it.coverId == selectedId) }
    }

    private val listDefaultCoverOutput = mutableListOf(
        AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1),
        AddEditTripViewModel.DefaultCoverElement(coverId = 2, isSelected = false, resId = 2),
        AddEditTripViewModel.DefaultCoverElement(coverId = 3, isSelected = false, resId = 3),
        AddEditTripViewModel.DefaultCoverElement(coverId = 4, isSelected = false, resId = 4),
        AddEditTripViewModel.DefaultCoverElement(coverId = 5, isSelected = false, resId = 5),
        AddEditTripViewModel.DefaultCoverElement(coverId = 6, isSelected = false, resId = 6),
        AddEditTripViewModel.DefaultCoverElement(coverId = 7, isSelected = false, resId = 7),
        AddEditTripViewModel.DefaultCoverElement(coverId = 8, isSelected = false, resId = 8),
        AddEditTripViewModel.DefaultCoverElement(coverId = 9, isSelected = false, resId = 9),
        AddEditTripViewModel.DefaultCoverElement(coverId = 10, isSelected = false, resId = 10)
    )
}