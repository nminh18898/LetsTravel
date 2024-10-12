package com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.manage_trip.test_helper.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.test_helper.FakeTripInfoRepository
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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
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

    private fun setupViewModel(tripIdArg: Long = 0L) {
        viewModel = AddEditTripViewModel(
            defaultCoverResourceProvider = fakeCoverDefaultResourceProvider,
            getListDefaultCoverUseCase = GetListDefaultCoverUseCase(fakeTripInfoRepository),
            createTripInfoUseCase = CreateTripInfoUseCase(fakeTripInfoRepository),
            getTripInfoUseCase = GetTripInfoUseCase(fakeTripInfoRepository),
            updateTripInfoUseCase = UpdateTripInfoUseCase(fakeTripInfoRepository),
            deleteTripInfoUseCase = DeleteTripInfoUseCase(fakeTripInfoRepository),
            savedStateHandle = SavedStateHandle(mapOf(MainAppRoute.tripIdArg to tripIdArg))
        )
    }

    @After
    fun cleanup() {
        fakeTripInfoRepository.reset()
    }

    @Test
    fun loadTripInfo_loading() = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel(tripIdArg = 1L)

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
        fakeTripInfoRepository.addTrip(tripInfo)

        // When
        setupViewModel(tripIdArg = 1L)

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
    fun loadTripInfo_tripHasCustomCover_tripShown() = runTest {
        // Given
        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam - Sai Gon",
            coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
            defaultCoverId = 0,
            customCoverPath = "/sdcard/something.jpg"
        )
        fakeTripInfoRepository.addTrip(tripInfo)

        // When
        setupViewModel(tripIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.tripTitle).isEqualTo(tripInfo.title)
        Truth.assertThat(uiState.listCoverItems).isEqualTo(
            getListDefaultCoverOutput(selectedId = 0).toMutableList().apply {
                add(
                    0, AddEditTripViewModel.CustomCoverPhotoElement(
                        uri = "/sdcard/something.jpg",
                        isSelected = true
                    )
                )
            }
        )
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
    fun loadTripInfo_emptyTrip_showNotFound() {
        // When
        setupViewModel(tripIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isTrue()
        Truth.assertThat(uiState.canDeleteTrip).isFalse()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.newCreatedTripUiState).isNull()
        Truth.assertThat(uiState.isTripUpdated).isFalse()
        Truth.assertThat(uiState.isTripDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateTrip_verifyCanNotDelete() {
        // Given
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.uiState.value.canDeleteTrip).isFalse()
    }

    @Test
    fun onCreateTrip_validTitle_validCover_allowSave() {
        // Given
        setupViewModel()

        // Verify initial state
        val initialUiState = viewModel.uiState.value
        Truth.assertThat(initialUiState.allowSaveContent).isFalse()

        // When
        viewModel.onTripTitleUpdated("Ho Chi Minh City - Vietnam")
        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.tripTitle).isEqualTo("Ho Chi Minh City - Vietnam")
        Truth.assertThat(uiState.listCoverItems).isEqualTo(getListDefaultCoverOutput(selectedId = 1))
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isFalse()
        Truth.assertThat(uiState.canDeleteTrip).isFalse()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.newCreatedTripUiState).isNull()
        Truth.assertThat(uiState.isTripUpdated).isFalse()
        Truth.assertThat(uiState.isTripDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isTrue()
    }

    @Test
    fun onCreateTrip_emptyTitle_disableSave() {
        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("Ho Chi Minh City")
        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))

        // Verify initial state
        val initialUiState = viewModel.uiState.value
        Truth.assertThat(initialUiState.canDeleteTrip).isFalse()
        Truth.assertThat(initialUiState.allowSaveContent).isTrue()

        // When
        viewModel.onTripTitleUpdated("")

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.tripTitle).isEmpty()
        Truth.assertThat(uiState.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateTrip_validTitle_emptyCover_disableSave() {
        // Given
        setupViewModel()

        // When - Only update title
        viewModel.onTripTitleUpdated("Vietnam")

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.tripTitle).isEqualTo("Vietnam")
        Truth.assertThat(uiState.allowSaveContent).isFalse()
    }

    @Test
    fun onPickNewPhoto_whenCreateNewTrip_newPhotoShown() {
        // Given
        setupViewModel()

        // When - select first photo
        viewModel.onNewPhotoPicked("/sdcard/first.jpg")

        // Then - verify that photo is displayed and selected
        Truth.assertThat(viewModel.uiState.value.listCoverItems).isEqualTo(
            getListDefaultCoverOutput(selectedId = 0).toMutableList().apply {
                add(
                    0, AddEditTripViewModel.CustomCoverPhotoElement(
                        uri = "/sdcard/first.jpg",
                        isSelected = true
                    )
                )
            }
        )

        // When - select another photo
        viewModel.onNewPhotoPicked("/sdcard/second.jpg")

        // Then - verify both photo is display, second photo is selected
        Truth.assertThat(viewModel.uiState.value.listCoverItems).isEqualTo(
            getListDefaultCoverOutput(selectedId = 0).toMutableList().apply {
                add(
                    0, AddEditTripViewModel.CustomCoverPhotoElement(
                        uri = "/sdcard/first.jpg",
                        isSelected = false
                    )
                )

                add(
                    0, AddEditTripViewModel.CustomCoverPhotoElement(
                        uri = "/sdcard/second.jpg",
                        isSelected = true
                    )
                )
            }
        )
    }

    @Test
    fun onPickNewPhoto_whenUpdateNewTrip_newPhotoShown() {
        // Given
        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
            defaultCoverId = 0,
            customCoverPath = "/sdcard/default.jpg"
        )
        fakeTripInfoRepository.addTrip(tripInfo)
        setupViewModel(1L)

        // When - select another photo
        viewModel.onNewPhotoPicked("/sdcard/new.jpg")

        // Then - verify new photo is displayed and selected
        Truth.assertThat(viewModel.uiState.value.listCoverItems).isEqualTo(
            getListDefaultCoverOutput(selectedId = 0).toMutableList().apply {
                add(
                    0, AddEditTripViewModel.CustomCoverPhotoElement(
                        uri = "/sdcard/default.jpg",
                        isSelected = false
                    )
                )

                add(
                    0, AddEditTripViewModel.CustomCoverPhotoElement(
                        uri = "/sdcard/new.jpg",
                        isSelected = true
                    )
                )
            }
        )
    }

    @Test
    fun onDeleteClick_onDeleteDismissed_verifyDeleteConfirmationIsShownThenHidden() {
        // Given
        setupViewModel()

        // When
        viewModel.onDeleteClick()

        // Then - verify confirmation is displayed
        Truth.assertThat(viewModel.uiState.value.isShowDeleteConfirmation).isTrue()

        // When
        viewModel.onDeleteDismiss()

        // Then - verify confirmation is hidden
        Truth.assertThat(viewModel.uiState.value.isShowDeleteConfirmation).isFalse()
    }

    @Test
    fun onDeleteClick_onDeleteConfirmed_verifyTripIsDeleted() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
            defaultCoverId = 0,
            customCoverPath = "/sdcard/default.jpg"
        )
        fakeTripInfoRepository.addTrip(tripInfo)
        setupViewModel(1L)

        // Verify delete confirmation is shown
        viewModel.onDeleteClick()
        Truth.assertThat(viewModel.uiState.value.isShowDeleteConfirmation).isTrue()

        // When
        viewModel.onDeleteConfirm()
        Truth.assertThat(viewModel.uiState.value.isLoading).isTrue()

        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isTripDeleted).isTrue()
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isNull()
    }

    @Test
    fun onDeleteConfirmed_errorOccurInRepository_showErrorInBrief() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
            defaultCoverId = 0,
            customCoverPath = "/sdcard/default.jpg"
        )
        fakeTripInfoRepository.addTrip(tripInfo)
        fakeTripInfoRepository.forceError = true
        setupViewModel(1L)

        // When
        viewModel.onDeleteConfirm()
        runCurrent()

        // Then - verify trip is not deleted and error is shown
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isTripDeleted).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_TRIP_INFO)
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isNotNull()

        advanceUntilIdle()
        // Then - verify error is hidden
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onSaveClick_whenCreateNewTrip_hasDefaultCover() {
        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("Vietnam")
        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))

        // When
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.newCreatedTripUiState).isEqualTo(NewCreatedTripUiState(1L))
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isEqualTo(
            TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            )
        )
    }

    @Test
    fun onSaveClick_whenCreateNewTrip_hasCustomCover() {
        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("Vietnam")
        viewModel.onNewPhotoPicked("/sdcard/default.jpg")

        // When
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.newCreatedTripUiState).isEqualTo(NewCreatedTripUiState(1L))
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isEqualTo(
            TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/default.jpg"
            )
        )
    }

    @Test
    fun onSaveClick_whenUpdateTrip_changeTitle_changeCoverFromDefaultToCustom() {
        // Given
        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
            defaultCoverId = 1,
            customCoverPath = ""
        )
        fakeTripInfoRepository.addTrip(tripInfo)
        setupViewModel(1L)

        // When
        viewModel.onTripTitleUpdated("Vietnam - Ho Chi Minh City")
        viewModel.onNewPhotoPicked("/sdcard/default.jpg")
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isTripUpdated).isTrue()
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isEqualTo(
            TripInfo(
                tripId = 1L,
                title = "Vietnam - Ho Chi Minh City",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/default.jpg"
            )
        )
    }

    @Test
    fun onSaveClick_whenUpdateTrip_changeTitle_changeCoverFromCustomToDefault() {
        // Given
        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
            defaultCoverId = 0,
            customCoverPath = "/sdcard/default.jpg"
        )
        fakeTripInfoRepository.addTrip(tripInfo)
        setupViewModel(1L)

        // When
        viewModel.onTripTitleUpdated("Vietnam - Ho Chi Minh City")
        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isTripUpdated).isTrue()
        Truth.assertThat(fakeTripInfoRepository.getTripInfo(1L)).isEqualTo(
            TripInfo(
                tripId = 1L,
                title = "Vietnam - Ho Chi Minh City",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            )
        )
    }

    @Test
    fun onSaveClick_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("Vietnam")
        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))

        // When
        val resultList = mutableListOf<AddEditTripInfoUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSaveClick()
        advanceUntilIdle()

        // Then
        Truth.assertThat(resultList).hasSize(3)
        // first item is the state before we invoke onSaveClick(), second and third item is the state after we invoke onSaveClick()
        // We need to assert loading is shown correctly
        Truth.assertThat(resultList[0].isLoading).isFalse()
        Truth.assertThat(resultList[1].isLoading).isTrue()
        Truth.assertThat(resultList[2].isLoading).isFalse()
    }

    @Test
    fun onSaveClick_emptyTitle_showError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("")

        // When
        val resultList = mutableListOf<AddEditTripInfoUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSaveClick()
        advanceUntilIdle()

        // Then
        Truth.assertThat(resultList).hasSize(3)
        // first item is the state before we invoke onSaveClick(), second and third item is the state after we invoke onSaveClick()
        // We need to assert error is shown correctly
        Truth.assertThat(resultList[0].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[1].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO)
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onSaveClick_emptyCover_showError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("Vietnam") // only set title, without set cover

        // When
        val resultList = mutableListOf<AddEditTripInfoUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSaveClick()
        advanceUntilIdle()

        // Then
        Truth.assertThat(resultList).hasSize(3)
        // first item is the state before we invoke onSaveClick(), second and third item is the state after we invoke onSaveClick()
        // We need to assert error is shown correctly
        Truth.assertThat(resultList[0].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[1].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO)
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onSaveClick_whenCreateNewTrip_errorFromRepository_showError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()
        viewModel.onTripTitleUpdated("Vietnam")
        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))
        fakeTripInfoRepository.forceError = true

        // When
        val resultList = mutableListOf<AddEditTripInfoUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSaveClick()
        advanceUntilIdle()

        // Then - assert loading and error is shown correctly
        Truth.assertThat(resultList).hasSize(4)

        // First item is the state before we invoke onSaveClick(),
        Truth.assertThat(resultList[0].isLoading).isFalse()

        // Second item: verify loading is shown right after click save
        Truth.assertThat(resultList[1].isLoading).isTrue()

        // Third item: error occurs, verify error state is shown
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    @Test
    fun onSaveClick_whenUpdateTrip_errorFromRepository_showError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        val tripInfo = TripInfo(
            tripId = 1L,
            title = "Vietnam",
            coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
            defaultCoverId = 0,
            customCoverPath = "/sdcard/default.jpg"
        )
        fakeTripInfoRepository.addTrip(tripInfo)
        setupViewModel(1L)

        viewModel.onCoverSelected(AddEditTripViewModel.DefaultCoverElement(coverId = 1, isSelected = false, resId = 1))
        viewModel.onTripTitleUpdated("Vietnam - Ho Chi Minh City")
        fakeTripInfoRepository.forceError = true

        // load trip info and update data on UI
        advanceUntilIdle()

        // When - run logic update trip in repository
        val resultList = mutableListOf<AddEditTripInfoUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSaveClick()
        advanceUntilIdle()

        // Then - assert loading and error is shown correctly
        Truth.assertThat(resultList).hasSize(4)

        // First item is the state before we invoke onSaveClick(),
        Truth.assertThat(resultList[0].isLoading).isFalse()

        // Second item: verify loading is shown right after click save
        Truth.assertThat(resultList[1].isLoading).isTrue()

        // Third item: error occurs, verify error state is shown
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_TRIP_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
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