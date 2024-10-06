package com.minhhnn18898.manage_trip.trip_detail.presentation.activity

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.CreateTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.DeleteTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.UpdateTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTripActivityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Mock
    private lateinit var dateTimeFormatter: TripDetailDateTimeFormatter

    private lateinit var mockAnnotations: AutoCloseable

    private lateinit var viewModel: AddEditTripActivityViewModel

    @Before
    fun setup() {
        mockAnnotations = MockitoAnnotations.openMocks(this)
        fakeTripDetailRepository = FakeTripDetailRepository()
    }

    private fun setupViewModel(activityIdArg: Long = 0L) {
        viewModel = AddEditTripActivityViewModel(
            savedStateHandle = SavedStateHandle(mapOf(MainAppRoute.tripIdArg to 1L, MainAppRoute.activityIdArg to activityIdArg)),
            createTripActivityInfoUseCase = CreateTripActivityInfoUseCase(fakeTripDetailRepository),
            getTripActivityInfoUseCase = GetTripActivityInfoUseCase(fakeTripDetailRepository),
            updateTripActivityInfoUseCase = UpdateTripActivityInfoUseCase(fakeTripDetailRepository),
            deleteTripActivityInfoUseCase = DeleteTripActivityInfoUseCase(fakeTripDetailRepository),
            dateTimeFormatter = dateTimeFormatter
        )
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
        mockAnnotations.close()
    }

    private fun createDefaultActivityInRepositoryForTesting(): TripActivityInfo {
        val activityInfo = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 15,
            timeTo = 26,
            price = 2_000_000,
        )

        fakeTripDetailRepository.upsertActivityInfo(1L, activityInfo)

        Mockito
            .`when`(dateTimeFormatter.getHourMinute(anyLong()))
            .thenAnswer {
                val param = it.arguments[0] as Long
                // Mocking get hours and minutes base on the first and last character value of input.
                // For example: 15 -> hour: 1, minutes: 5
                Pair(param.toString().first().digitToInt(), param.toString().last().digitToInt())
            }

        return activityInfo
    }

    @Test
    fun loadActivityInfo_loading() = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel(activityIdArg = 1L)

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.uiState.value.isLoading).isTrue()

        // When: execute pending coroutines actions
        advanceUntilIdle()

        // Then: progress indicator is hidden
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun loadActivityInfo_activityShown() {
        // Given
        val activityInfo = createDefaultActivityInRepositoryForTesting()

        // When
        setupViewModel(activityIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        val activityUiState = uiState.tripActivityUiState

        Truth.assertThat(activityUiState.photo).isEqualTo(activityInfo.photo)
        Truth.assertThat(activityUiState.name).isEqualTo(activityInfo.title)
        Truth.assertThat(activityUiState.description).isEqualTo(activityInfo.description)
        Truth.assertThat(activityUiState.prices).isEqualTo(activityInfo.price.toString())
        Truth.assertThat(activityUiState.date).isEqualTo(activityInfo.timeFrom)
        Truth.assertThat(activityUiState.timeFrom).isEqualTo(Pair(1, 5))
        Truth.assertThat(activityUiState.timeTo).isEqualTo(Pair(2, 6))

        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isFalse()
        Truth.assertThat(uiState.canDelete).isTrue()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.isCreated).isFalse()
        Truth.assertThat(uiState.isUpdated).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isTrue()
    }

    @Test
    fun loadActivityInfo_emptyActivity_showNotFound() {
        // When
        setupViewModel(activityIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isTrue()
        Truth.assertThat(uiState.canDelete).isFalse()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.isCreated).isFalse()
        Truth.assertThat(uiState.isUpdated).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateNewActivity_verifyCanNotDelete() {
        // Given
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.uiState.value.canDelete).isFalse()
    }

    @Test
    fun onPhotoUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onPhotoUpdated("/sdcard/something.jpg")

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.photo).isEqualTo("/sdcard/something.jpg")
    }

    @Test
    fun onNameUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onNameUpdated("Discover the Delta's Charms")

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.name).isEqualTo("Discover the Delta's Charms")
    }

    @Test
    fun onDescriptionUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onDescriptionUpdated("Mekong Delta Tour from HCM City")

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.description).isEqualTo("Mekong Delta Tour from HCM City")
    }

    @Test
    fun onPricesUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onPricesUpdated("1200000")

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.prices).isEqualTo("1200000")
    }

    @Test
    fun onDateUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onDateUpdated(1200)

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.date).isEqualTo(1200)
    }

    @Test
    fun onTimeFromUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onTimeFromUpdated(Pair(1, 2))

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.timeFrom).isEqualTo(Pair(1, 2))
    }

    @Test
    fun onTimeToUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onTimeToUpdated(Pair(3, 4))

        // Then
        Truth.assertThat(viewModel.uiState.value.tripActivityUiState.timeTo).isEqualTo(Pair(3, 4))
    }

    @Test
    fun onCreateActivity_validActivityName_validSchedule_verifyAllowSave() {
        // Given
        setupViewModel()

        // Verify initial state: not allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()

        // When
        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms")
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()
    }

    @Test
    fun onCreateActivity_validActivityName_invalidSchedule_verifyNotAllowSave() {
        // Given
        setupViewModel()

        // When - set invalid schedule
        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms")
            onTimeFromUpdated(Pair(1, 5))
            onTimeToUpdated(Pair(1, 2))
        }

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateActivity_missingActivityName_verifyNotAllowSave() {
        // Given
        setupViewModel()

        // Initial state: has full info
        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms")
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }

        // Verify initial state: allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When - set empty name
        viewModel.onNameUpdated("")

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
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
    fun onDeleteClick_onDeleteConfirmed_verifyActivityIsDeleted() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        createDefaultActivityInRepositoryForTesting()
        setupViewModel(activityIdArg = 1L)

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
        Truth.assertThat(uiState.isDeleted).isTrue()
        Truth.assertThat(uiState.isNotFound).isTrue()
        Truth.assertThat(fakeTripDetailRepository.getActivityInfoForTesting(1L)).isNull()
    }

    @Test
    fun onDeleteConfirmed_errorOccurInRepository_showErrorInBrief() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        createDefaultActivityInRepositoryForTesting()
        fakeTripDetailRepository.forceError = true
        setupViewModel(activityIdArg = 1L)

        // When
        viewModel.onDeleteConfirm()
        runCurrent()

        // Then - verify activity is not deleted and error is shown
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_ACTIVITY_INFO)
        Truth.assertThat(fakeTripDetailRepository.getActivityInfo(1L)).isNotNull()

        advanceUntilIdle()

        // Then - verify error is hidden
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onDeleteClick_onDeleteConfirmed_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        createDefaultActivityInRepositoryForTesting()
        setupViewModel(activityIdArg = 1L)
        viewModel.onDeleteClick()
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditTripActivityUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onDeleteConfirm()
        advanceUntilIdle()

        // Then - assert loading is shown correctly
        // First item is the state before we invoke onDeleteConfirm(),
        Truth.assertThat(resultList[0].isLoading).isFalse()

        // Second item is when we hide the confirmation view
        Truth.assertThat(resultList[1].isLoading).isFalse()

        // Third item is when we display the loading view
        Truth.assertThat(resultList[2].isLoading).isTrue()

        // Third item is when we hide the loading view
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    @Test
    fun onSaveClick_createNewActivity_verifyNewActivityCreated() {
        // Given
        setupViewModel()
        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms")
            onDescriptionUpdated("Mekong Delta Tour from HCM City")
            onPhotoUpdated("/sdcard/something.jpg")
            onPricesUpdated("1000000")
            onDateUpdated(10)
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }

        Mockito
            .`when`(dateTimeFormatter.combineHourMinutesDayToMillis(anyLong(), anyInt(), anyInt()))
            .thenAnswer {
                val dateParam = it.arguments[0] as Long
                val hourParam = it.arguments[1] as Int
                val minuteParam = it.arguments[2] as Int
                // Mocking flow combine date value and (hour, minute) with the formula: (date * 10) + (hour * 5) + minute
                // For example: date 10, hour: 1, minutes: 5 -> 100 + 5 + 5 = 110
                dateParam * 10 + hourParam * 5 + minuteParam
            }

        // When
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isCreated).isTrue()
        Truth.assertThat(fakeTripDetailRepository.getActivityInfoForTesting(1L)).isEqualTo(
            TripActivityInfo(
                activityId = 1L,
                title = "Discover the Delta's Charms",
                description = "Mekong Delta Tour from HCM City",
                photo = "/sdcard/something.jpg",
                price = 1_000_000,
                timeFrom = 107,
                timeTo = 110
            )
        )
    }

    @Test
    fun onSaveClick_updatedActivity_verifyNewInfoUpdated() {
        // Given
        createDefaultActivityInRepositoryForTesting()
        setupViewModel(1L)

        Mockito
            .`when`(dateTimeFormatter.combineHourMinutesDayToMillis(anyLong(), anyInt(), anyInt()))
            .thenAnswer {
                val dateParam = it.arguments[0] as Long
                val hourParam = it.arguments[1] as Int
                val minuteParam = it.arguments[2] as Int
                // Mocking flow combine date value and (hour, minute) with the formula: (date * 10) + (hour * 5) + minute
                // For example: date 10, hour: 1, minutes: 5 -> 100 + 5 + 5 = 110
                dateParam * 10 + hourParam * 5 + minuteParam
            }

        // When
        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms - new")
            onDescriptionUpdated("Mekong Delta Tour from HCM City - new")
            onPhotoUpdated("/sdcard/something_new.jpg")
            onPricesUpdated("1700000")
            onDateUpdated(10)
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }

        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isUpdated).isTrue()
        Truth.assertThat(fakeTripDetailRepository.getActivityInfoForTesting(1L)).isEqualTo(
            TripActivityInfo(
                activityId = 1L,
                title = "Discover the Delta's Charms - new",
                description = "Mekong Delta Tour from HCM City - new",
                photo = "/sdcard/something_new.jpg",
                price = 1_700_000,
                timeFrom = 107,
                timeTo = 110
            )
        )
    }

    @Test
    fun onSaveClick_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()

        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms - new")
            onDescriptionUpdated("Mekong Delta Tour from HCM City - new")
            onPhotoUpdated("/sdcard/something_new.jpg")
            onPricesUpdated("1700000")
            onDateUpdated(10)
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditTripActivityUiState>()
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
    fun onSaveClick_whenCreateNewActivity_errorFromRepository_verifyShowError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()

        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms - new")
            onDescriptionUpdated("Mekong Delta Tour from HCM City - new")
            onPhotoUpdated("/sdcard/something_new.jpg")
            onPricesUpdated("1700000")
            onDateUpdated(10)
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }

        fakeTripDetailRepository.forceError = true
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditTripActivityUiState>()
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
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_ACTIVITY_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    @Test
    fun onSaveClick_whenUpdateActivity_errorFromRepository_verifyShowError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        createDefaultActivityInRepositoryForTesting()
        fakeTripDetailRepository.forceError = true
        setupViewModel(1L)

        // load trip info and update data on UI
        advanceUntilIdle()

        // When
        viewModel.apply {
            onNameUpdated("Discover the Delta's Charms - new")
            onDescriptionUpdated("Mekong Delta Tour from HCM City - new")
            onPhotoUpdated("/sdcard/something_new.jpg")
            onPricesUpdated("1700000")
            onDateUpdated(10)
            onTimeFromUpdated(Pair(1, 2))
            onTimeToUpdated(Pair(1, 5))
        }

        // Input new info for activity info
        advanceUntilIdle()

        val resultList = mutableListOf<AddEditTripActivityUiState>()
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
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_ACTIVITY_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }
}