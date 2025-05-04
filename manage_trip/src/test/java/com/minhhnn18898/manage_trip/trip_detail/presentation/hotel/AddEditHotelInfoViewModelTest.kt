package com.minhhnn18898.manage_trip.trip_detail.presentation.hotel

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel.CreateNewHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel.DeleteHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel.GetHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.hotel.UpdateHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.hotel.AddEditHotelInfoViewModel
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.hotel.AddEditHotelUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.hotel.HotelUiState
import com.minhhnn18898.test_utils.MainDispatcherRule
import com.minhhnn18898.trip_data.model.plan.HotelInfo
import com.minhhnn18898.trip_data.test_helper.FakeTripDetailRepository
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

@Suppress("SpellCheckingInspection")
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditHotelInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    private lateinit var viewModel: AddEditHotelInfoViewModel

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
    }

    private fun setupViewModel(hotelIdArg: Long = 0L) {
        viewModel = AddEditHotelInfoViewModel(
            savedStateHandle = SavedStateHandle(mapOf(MainAppRoute.tripIdArg to 1L, MainAppRoute.hotelIdArg to hotelIdArg)),
            createNewHotelInfoUseCase = CreateNewHotelInfoUseCase(fakeTripDetailRepository),
            getHotelInfoUseCase = GetHotelInfoUseCase(fakeTripDetailRepository),
            updateHotelInfoUseCase = UpdateHotelInfoUseCase(fakeTripDetailRepository),
            deleteHotelInfoUseCase = DeleteHotelInfoUseCase(fakeTripDetailRepository)
        )
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun loadHotelInfo_loading()  = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel(hotelIdArg = 1L)

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.uiState.value.isLoading).isTrue()

        // When: execute pending coroutines actions
        advanceUntilIdle()

        // Then: progress indicator is hidden
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun loadHotelInfo_hotelShown() {
        // Given
        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(1L, hotelInfo)

        // When
        setupViewModel(hotelIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        assertHotelUiState(viewModel.uiState.value.hotelUiState, hotelInfo)
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isFalse()
        Truth.assertThat(uiState.canDelete).isTrue()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.isCreated).isFalse()
        Truth.assertThat(uiState.isUpdated).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isTrue()
    }

    @Test
    fun loadHotelInfo_emptyHotel_showNotFound() {
        // When
        setupViewModel(hotelIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isTrue()
        Truth.assertThat(uiState.canDelete).isFalse()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.isCreated).isFalse()
        Truth.assertThat(uiState.isUpdated).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateHotel_verifyCanNotDelete() {
        // Given
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.uiState.value.canDelete).isFalse()
    }

    @Test
    fun onHotelNameUpdated_newInfoShown() {
        // Given
        setupViewModel()

        // When
        viewModel.onHotelNameUpdated("Amber Inn")

        // Then
        Truth.assertThat(viewModel.uiState.value.hotelUiState.hotelName).isEqualTo("Amber Inn")
    }

    @Test
    fun onAddressUpdated_newInfoShown() {
        // Given
        setupViewModel()

        // When
        viewModel.onAddressUpdated("Bangkok - Thailand")

        // Then
        Truth.assertThat(viewModel.uiState.value.hotelUiState.address).isEqualTo("Bangkok - Thailand")
    }

    @Test
    fun onPricesUpdated_newInfoShown() {
        // Given
        setupViewModel()

        // When
        viewModel.onPricesUpdated("220000000")

        // Then
        Truth.assertThat(viewModel.uiState.value.hotelUiState.prices).isEqualTo("220000000")
    }

    @Test
    fun onCheckInDateUpdated_newInfoShown() {
        // Given
        setupViewModel()

        // When
        viewModel.onCheckInDateUpdated(100_000)

        // Then
        Truth.assertThat(viewModel.uiState.value.hotelUiState.checkInDate).isEqualTo(100_000)
    }

    @Test
    fun onCheckOutDateUpdated_newInfoShown() {
        // Given
        setupViewModel()

        // When
        viewModel.onCheckOutDateUpdated(200_000)

        // Then
        Truth.assertThat(viewModel.uiState.value.hotelUiState.checkOutDate).isEqualTo(200_000)
    }

    @Test
    fun onCreateHotel_validHotelName_validCheckInAndCheckoutDate_verifyAllowSave() {
        // Given
        setupViewModel()

        // Verify initial state: not allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()

        // When
        viewModel.onHotelNameUpdated("Amber Inn")
        viewModel.onCheckInDateUpdated(1_000_000)
        viewModel.onCheckOutDateUpdated(2_000_000)

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()
    }

    @Test
    fun onCreateHotel_validHotelName_checkOutDateBeforeCheckInDate_verifyNotAllowSave() {
        // Given
        setupViewModel()

        // Verify initial state: not allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()

        // When
        viewModel.apply {
            onHotelNameUpdated("Amber Inn")
            onCheckInDateUpdated(2_000_000)
            onCheckOutDateUpdated(1_000_000)
        }

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateHotel_validHotelName_checkOutDateBeforeCheckInDate_verifyShowErrorInBriefPeriod() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())
        setupViewModel()

        // Verify initial state: not allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()

        // When
        viewModel.apply {
            onHotelNameUpdated("Amber Inn")
            onCheckInDateUpdated(2_000_000)
            onCheckOutDateUpdated(1_000_000)
        }

        runCurrent()

        // Then - verify error is shown
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_STAY_DURATION_IS_NOT_VALID)
        advanceUntilIdle()
        // Verify error is hidden
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onCreateHotel_missingHotelName_verifyNotAllowSave() {
        // Given
        setupViewModel()

        // Initial state: has full info
        viewModel.apply {
            onHotelNameUpdated("Amber Inn")
            onCheckInDateUpdated(1_000_000)
            onCheckOutDateUpdated(2_000_000)
        }

        // Verify initial state: allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When - set empty name
        viewModel.onHotelNameUpdated("")

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateHotel_missingCheckInDate_verifyNotAllowSave() {
        // Given
        setupViewModel()

        // Initial state: has full info
        viewModel.apply {
            onHotelNameUpdated("Amber Inn")
            onCheckInDateUpdated(1_000_000)
            onCheckOutDateUpdated(2_000_000)
        }

        // Verify initial state: allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When - set empty name
        viewModel.onCheckInDateUpdated(null)

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateHotel_missingCheckOutDate_verifyNotAllowSave() {
        // Given
        setupViewModel()

        // Initial state: has full info
        viewModel.apply {
            onHotelNameUpdated("Amber Inn")
            onCheckInDateUpdated(1_000_000)
            onCheckOutDateUpdated(2_000_000)
        }

        // Verify initial state: allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When - set empty name
        viewModel.onCheckOutDateUpdated(null)

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
    fun onDeleteClick_onDeleteConfirmed_verifyHotelIsDeleted() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(1L, hotelInfo)
        setupViewModel(hotelIdArg = 1L)

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
        Truth.assertThat(fakeTripDetailRepository.getHotelInfoForTesting(1L)).isNull()
    }

    @Test
    fun onDeleteConfirmed_errorOccurInRepository_showErrorInBrief() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(1L, hotelInfo)
        fakeTripDetailRepository.forceError = true
        setupViewModel(hotelIdArg = 1L)

        // When
        viewModel.onDeleteConfirm()
        runCurrent()

        // Then - verify hotel is not deleted and error is shown
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_HOTEL_INFO)
        Truth.assertThat(fakeTripDetailRepository.getHotelInfoForTesting(1L)).isNotNull()

        advanceUntilIdle()
        // Then - verify error is hidden
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onDeleteClick_onDeleteConfirmed_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(1L, hotelInfo)
        setupViewModel(hotelIdArg = 1L)
        viewModel.onDeleteClick()
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditHotelUiState>()
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
    fun onSaveClick_createNewHotel_verifyNewHotelCreated() {
        // Given
        setupViewModel()
        viewModel.apply {
            onHotelNameUpdated("Amber Inn")
            onAddressUpdated("Bangkok")
            onPricesUpdated("1000000")
            onCheckInDateUpdated(1_000_000)
            onCheckOutDateUpdated(2_000_000)
        }

        // When
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isCreated).isTrue()
        Truth.assertThat(fakeTripDetailRepository.getHotelInfoForTesting(1L)).isEqualTo(
            HotelInfo(
                hotelId = 1L,
                hotelName = "Amber Inn",
                address = "Bangkok",
                checkInDate = 1_000_000,
                checkOutDate = 2_000_000,
                price = 1_000_000
            )
        )
    }

    @Test
    fun onSaveClick_updateHotel_verifyInfoUpdated() {
        // Given
        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(1L, hotelInfo)

        setupViewModel(hotelIdArg = 1L)

        // When
        viewModel.apply {
            onHotelNameUpdated("Le Saigon Hotel")
            onAddressUpdated("Tan Binh District, Ho Chi Minh City")
            onPricesUpdated("1200000")
            onCheckInDateUpdated(1_100_000)
            onCheckOutDateUpdated(1_500_000)
        }

        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isUpdated).isTrue()
        Truth.assertThat(fakeTripDetailRepository.getHotelInfoForTesting(1L)).isEqualTo(
            HotelInfo(
                hotelId = 1L,
                hotelName = "Le Saigon Hotel",
                address = "Tan Binh District, Ho Chi Minh City",
                checkInDate = 1_100_000,
                checkOutDate = 1_500_000,
                price = 1_200_000
            )
        )
    }

    @Test
    fun onSaveClick_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()
        viewModel.apply {
            onHotelNameUpdated("Le Saigon Hotel")
            onAddressUpdated("Tan Binh District, Ho Chi Minh City")
            onPricesUpdated("1200000")
            onCheckInDateUpdated(1_100_000)
            onCheckOutDateUpdated(1_500_000)
        }

        // When
        val resultList = mutableListOf<AddEditHotelUiState>()
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
    fun onSaveClick_whenCreateNewHotel_errorFromRepository_verifyShowError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()

        viewModel.apply {
            onHotelNameUpdated("Le Saigon Hotel")
            onAddressUpdated("Tan Binh District, Ho Chi Minh City")
            onPricesUpdated("1200000")
            onCheckInDateUpdated(1_100_000)
            onCheckOutDateUpdated(1_500_000)
        }

        fakeTripDetailRepository.forceError = true

        // When
        val resultList = mutableListOf<AddEditHotelUiState>()
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
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_HOTEL_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    @Test
    fun onSaveClick_whenUpdateHotel_errorFromRepository_verifyShowError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        val hotelInfo = HotelInfo(
            hotelId = 1L,
            hotelName = "Le Saigon Hotel",
            address = "Tan Binh District, Ho Chi Minh City",
            checkInDate = 1_100_000,
            checkOutDate = 1_500_000,
            price = 1_200_000
        )
        fakeTripDetailRepository.upsertHotelInfo(1L, hotelInfo)
        fakeTripDetailRepository.forceError = true
        setupViewModel(1L)

        // load trip info and update data on UI
        advanceUntilIdle()

        // When
        viewModel.apply {
            onHotelNameUpdated("Le Saigon Hotel - new")
            onAddressUpdated("Tan Binh District, Ho Chi Minh City - new")
            onPricesUpdated("1500000")
            onCheckInDateUpdated(1_500_000)
            onCheckOutDateUpdated(2_500_000)
        }

        // Input new info for hotel info
        advanceUntilIdle()

        val resultList = mutableListOf<AddEditHotelUiState>()
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
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_HOTEL_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    private fun assertHotelUiState(uiState: HotelUiState, hotelInfo: HotelInfo) {
        Truth.assertThat(uiState.hotelName).isEqualTo(hotelInfo.hotelName)
        Truth.assertThat(uiState.address).isEqualTo(hotelInfo.address)
        Truth.assertThat(uiState.prices).isEqualTo(hotelInfo.price.toString())
        Truth.assertThat(uiState.checkInDate).isEqualTo(hotelInfo.checkInDate)
        Truth.assertThat(uiState.checkOutDate).isEqualTo(hotelInfo.checkOutDate)
    }
}