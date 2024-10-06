@file:Suppress("SpellCheckingInspection")

package com.minhhnn18898.manage_trip.trip_detail.presentation.flight

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.CreateNewFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.DeleteFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.UpdateFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.flight.AddEditFlightInfoViewModel.ItineraryType
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import com.minhhnn18898.manage_trip.trip_detail.utils.assertFlightAndAirportEqual
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
class AddEditFlightInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Mock
    private lateinit var dateTimeFormatter: TripDetailDateTimeFormatter

    private lateinit var mockAnnotations: AutoCloseable

    private lateinit var viewModel: AddEditFlightInfoViewModel

    @Before
    fun setup() {
        mockAnnotations = MockitoAnnotations.openMocks(this)
        fakeTripDetailRepository = FakeTripDetailRepository()
    }

    private fun setupViewModel(flightIdArg: Long = 0L) {
        viewModel = AddEditFlightInfoViewModel(
            savedStateHandle = SavedStateHandle(mapOf(MainAppRoute.tripIdArg to 1L, MainAppRoute.flightIdArg to flightIdArg)),
            createNewFlightInfoUseCase = CreateNewFlightInfoUseCase(fakeTripDetailRepository),
            getFlightInfoUseCase = GetFlightInfoUseCase(fakeTripDetailRepository),
            updateFlightInfoUseCase = UpdateFlightInfoUseCase(fakeTripDetailRepository),
            deleteFlightInfoUseCase = DeleteFlightInfoUseCase(fakeTripDetailRepository),
            dateTimeFormatter = dateTimeFormatter
        )
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
        mockAnnotations.close()
    }

    private fun createDefaultFlightInfoInRepositoryForTesting(): Triple<FlightInfo, AirportInfo, AirportInfo> {
        val flightInfo = FlightInfo(
            flightId = 1L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_004,
            arrivalTime = 1_200_006,
            price = 2_000_000
        )

        val departAirport = AirportInfo(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        val destinationAirport = AirportInfo(
            code = "SIN",
            city = "Singapore",
            airportName = "Changi"
        )

        fakeTripDetailRepository.upsertFlightInfo(
            tripId = 1L,
            flightInfo = flightInfo,
            departAirport = departAirport,
            destinationAirport = destinationAirport
        )

        return Triple(flightInfo, departAirport, destinationAirport)
    }

    private fun mockDateTimeFormatter() {
        Mockito
            .`when`(dateTimeFormatter.getHourMinute(anyLong()))
            .thenAnswer {
                val param = it.arguments[0] as Long
                // Mocking get hours and minutes base on the first and last character value of input.
                // For example: 15 -> hour: 1, minutes: 5
                Pair(param.toString().first().digitToInt(), param.toString().last().digitToInt())
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
    }

    @Test
    fun loadFlightInfo_loading() = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel(flightIdArg = 1L)

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.uiState.value.isLoading).isTrue()

        // When: execute pending coroutines actions
        advanceUntilIdle()

        // Then: progress indicator is hidden
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun loadFlightInfo_flightInfoShown() {
        val flightAndAirportInfo = createDefaultFlightInfoInRepositoryForTesting()
        mockDateTimeFormatter()

        // When
        setupViewModel(flightIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        val flightUiState = uiState.flightUiState

        Truth.assertThat(flightUiState.flightNumber).isEqualTo(flightAndAirportInfo.first.flightNumber)
        Truth.assertThat(flightUiState.operatedAirlines).isEqualTo(flightAndAirportInfo.first.operatedAirlines)
        Truth.assertThat(flightUiState.prices).isEqualTo(flightAndAirportInfo.first.price.toString())

        Truth.assertThat(flightUiState.flightTime[ItineraryType.DEPARTURE]).isEqualTo(Pair(1, 4))
        Truth.assertThat(flightUiState.flightDate[ItineraryType.DEPARTURE]).isEqualTo(flightAndAirportInfo.first.departureTime)
        Truth.assertThat(flightUiState.flightTime[ItineraryType.ARRIVAL]).isEqualTo(Pair(1, 6))
        Truth.assertThat(flightUiState.flightDate[ItineraryType.ARRIVAL]).isEqualTo(flightAndAirportInfo.first.arrivalTime)

        Truth.assertThat(flightUiState.airportCodes[ItineraryType.DEPARTURE]).isEqualTo(flightAndAirportInfo.second.code)
        Truth.assertThat(flightUiState.airportNames[ItineraryType.DEPARTURE]).isEqualTo(flightAndAirportInfo.second.airportName)
        Truth.assertThat(flightUiState.airportCities[ItineraryType.DEPARTURE]).isEqualTo(flightAndAirportInfo.second.city)
        Truth.assertThat(flightUiState.airportCodes[ItineraryType.ARRIVAL]).isEqualTo(flightAndAirportInfo.third.code)
        Truth.assertThat(flightUiState.airportNames[ItineraryType.ARRIVAL]).isEqualTo(flightAndAirportInfo.third.airportName)
        Truth.assertThat(flightUiState.airportCities[ItineraryType.ARRIVAL]).isEqualTo(flightAndAirportInfo.third.city)

        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isFalse()
        Truth.assertThat(uiState.canDelete).isTrue()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.isCreated).isFalse()
        Truth.assertThat(uiState.isUpdated).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isTrue()
    }

    @Test
    fun loadFlightInfo_emptyFlight_showNotFound() {
        // When
        setupViewModel(flightIdArg = 1L)

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isTrue()
        Truth.assertThat(uiState.canDelete).isFalse()
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.isCreated).isFalse()
        Truth.assertThat(uiState.isUpdated).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateNewFlight_verifyCanNotDelete() {
        // Given
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.uiState.value.canDelete).isFalse()
    }

    @Test
    fun onFlightNumberUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onFlightNumberUpdated("VN 393")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.flightNumber).isEqualTo("VN 393")
    }

    @Test
    fun onAirlinesUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirlinesUpdated("Vietnam Airlines")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.operatedAirlines).isEqualTo("Vietnam Airlines")
    }

    @Test
    fun onPricesUpdated() {
        // Given
        setupViewModel()

        // When
        viewModel.onPricesUpdated("200000")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.prices).isEqualTo("200000")
    }

    @Test
    fun onAirportCodeUpdated_departureAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.airportCodes[ItineraryType.DEPARTURE]).isEqualTo("SGN")
    }

    @Test
    fun onAirportCodeUpdated_arrivalAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.airportCodes[ItineraryType.ARRIVAL]).isEqualTo("SIN")
    }

    @Test
    fun onAirportNameUpdated_departureAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirportNameUpdated(ItineraryType.DEPARTURE, "Tan Son Nhat")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.airportNames[ItineraryType.DEPARTURE]).isEqualTo("Tan Son Nhat")
    }

    @Test
    fun onAirportNameUpdated_arrivalAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirportNameUpdated(ItineraryType.ARRIVAL, "Changi")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.airportNames[ItineraryType.ARRIVAL]).isEqualTo("Changi")
    }

    @Test
    fun onAirportCityUpdated_departureAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirportCityUpdated(ItineraryType.DEPARTURE, "Ho Chi Minh City")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.airportCities[ItineraryType.DEPARTURE]).isEqualTo("Ho Chi Minh City")
    }

    @Test
    fun onAirportCityUpdated_arrivalAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onAirportCityUpdated(ItineraryType.ARRIVAL, "Singapore")

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.airportCities[ItineraryType.ARRIVAL]).isEqualTo("Singapore")
    }

    @Test
    fun onFlightDateUpdated_departureAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000)

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.flightDate[ItineraryType.DEPARTURE]).isEqualTo(1_000)
    }

    @Test
    fun onFlightDateUpdated_arrivalAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onFlightDateUpdated(ItineraryType.ARRIVAL, 2_000)

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.flightDate[ItineraryType.ARRIVAL]).isEqualTo(2_000)
    }

    @Test
    fun onFlightDateUpdated_invalidDayWhenUpdateArrivalAirport_showErrorInBrief() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()
        viewModel.onFlightDateUpdated(ItineraryType.DEPARTURE, 2_000)
        advanceUntilIdle()

        // When
        viewModel.onFlightDateUpdated(ItineraryType.ARRIVAL, 1_000)
        runCurrent()

        // Then
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_FLIGHT_TIME_IS_NOT_VALID)
        advanceUntilIdle()
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onFlightTimeUpdated_departureAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.flightTime[ItineraryType.DEPARTURE]).isEqualTo(Pair(1, 2))
    }

    @Test
    fun onFlightTimeUpdated_arrivalAirport() {
        // Given
        setupViewModel()

        // When
        viewModel.onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))

        // Then
        Truth.assertThat(viewModel.uiState.value.flightUiState.flightTime[ItineraryType.ARRIVAL]).isEqualTo(Pair(4, 5))
    }

    @Test
    fun onCreateFlight_validFlightNumber_validAirport_validSchedule_verifyAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Verify initial state: not allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()

        // When
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()
    }

    @Test
    fun onCreateFlight_missingFlightNumber_verifyNotAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Initial state: has full into, verify allow save
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When
        viewModel.onFlightNumberUpdated("")

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateFlight_missingDepartAiportCode_verifyNotAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Initial state: has full into, verify allow save
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When
        viewModel.onAirportCodeUpdated(ItineraryType.DEPARTURE, "")

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateFlight_missingArrivalAiportCode_verifyNotAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Initial state: has full into, verify allow save
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When
        viewModel.onAirportCodeUpdated(ItineraryType.ARRIVAL, "")

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateFlight_missingDepartFlightDate_verifyNotAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Initial state: has full into, verify allow save
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When
        viewModel.onFlightDateUpdated(ItineraryType.DEPARTURE, null)

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateFlight_missingArrivalFlightDate_verifyNotAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Initial state: has full into, verify allow save
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When
        viewModel.onFlightDateUpdated(ItineraryType.ARRIVAL, null)

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateFlight_invalidFlightSchedule_verifyNotAllowSave() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        // Initial state: has full into, verify allow save
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_000_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))
            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_200_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // When
        viewModel.apply {
            onFlightDateUpdated(ItineraryType.DEPARTURE, 1_200_000)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(4, 5))

            onFlightDateUpdated(ItineraryType.ARRIVAL, 1_000_000)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(1, 2))
        }

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

        createDefaultFlightInfoInRepositoryForTesting()
        mockDateTimeFormatter()

        setupViewModel(flightIdArg = 1L)

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
        Truth.assertThat(fakeTripDetailRepository.getFlightAirportInfo(1L)).isNull()
    }

    @Test
    fun onDeleteConfirmed_errorOccurInRepository_showErrorInBrief() = runTest {
        // Given
        Dispatchers.setMain(StandardTestDispatcher())

        createDefaultFlightInfoInRepositoryForTesting()
        mockDateTimeFormatter()

        fakeTripDetailRepository.forceError = true
        setupViewModel(flightIdArg = 1L)

        // When
        viewModel.onDeleteConfirm()
        runCurrent()

        // Then - verify activity is not deleted and error is shown
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isShowDeleteConfirmation).isFalse()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isDeleted).isFalse()
        Truth.assertThat(uiState.showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_FLIGHT_INFO)
        Truth.assertThat(fakeTripDetailRepository.getActivityInfo(1L)).isNotNull()

        advanceUntilIdle()

        // Then - verify error is hidden
        Truth.assertThat(viewModel.uiState.value.showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
    }

    @Test
    fun onDeleteClick_onDeleteConfirmed_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        createDefaultFlightInfoInRepositoryForTesting()
        mockDateTimeFormatter()

        setupViewModel(flightIdArg = 1L)
        viewModel.onDeleteClick()
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditFlightUiState>()
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
    fun onSaveClick_createNewFlightInfo_verifyNewFlightCreated() {
        // Given
        mockDateTimeFormatter()
        setupViewModel()

        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirlinesUpdated("Vietnam Airlines")
            onPricesUpdated("2000000")

            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onAirportNameUpdated(ItineraryType.DEPARTURE,"Tan Son Nhat")
            onAirportCityUpdated(ItineraryType.DEPARTURE, "Ho Chi Minh City")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 10)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))

            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onAirportNameUpdated(ItineraryType.ARRIVAL,"Changi")
            onAirportCityUpdated(ItineraryType.ARRIVAL, "Singapore")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 15)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        // When
        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isCreated).isTrue()

        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = FlightInfo(
                    flightId = 1L,
                    flightNumber = "VN 393",
                    operatedAirlines = "Vietnam Airlines",
                    departureTime = 107,
                    arrivalTime = 175,
                    price = 2_000_000
                ),
                departAirport = AirportInfo(
                    code = "SGN",
                    city = "Ho Chi Minh City",
                    airportName = "Tan Son Nhat"
                ),
                destinationAirport = AirportInfo(
                    code = "SIN",
                    city = "Singapore",
                    airportName = "Changi"
                )
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(1L)
        )
    }

    @Test
    fun onSaveClick_updatedFlightInfo_verifyNewInfoUpdated() {
        // Given
        mockDateTimeFormatter()
        createDefaultFlightInfoInRepositoryForTesting()
        setupViewModel(1L)

        // When
        viewModel.apply {
            onFlightNumberUpdated("VN 999")
            onAirlinesUpdated("Vietnam Airlines - new")
            onPricesUpdated("3000000")

            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN - new")
            onAirportNameUpdated(ItineraryType.DEPARTURE,"Tan Son Nhat - new")
            onAirportCityUpdated(ItineraryType.DEPARTURE, "Ho Chi Minh City - new")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 20)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(2, 3))

            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN - new")
            onAirportNameUpdated(ItineraryType.ARRIVAL,"Changi - new")
            onAirportCityUpdated(ItineraryType.ARRIVAL, "Singapore - new")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 30)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(5, 6))
        }

        viewModel.onSaveClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isUpdated).isTrue()

        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = FlightInfo(
                    flightId = 1L,
                    flightNumber = "VN 999",
                    operatedAirlines = "Vietnam Airlines - new",
                    departureTime = 213,
                    arrivalTime = 331,
                    price = 3_000_000
                ),
                departAirport = AirportInfo(
                    code = "SGN - new",
                    city = "Ho Chi Minh City - new",
                    airportName = "Tan Son Nhat - new"
                ),
                destinationAirport = AirportInfo(
                    code = "SIN - new",
                    city = "Singapore - new",
                    airportName = "Changi - new"
                )
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(1L)
        )
    }

    @Test
    fun onSaveClick_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        setupViewModel()

        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirlinesUpdated("Vietnam Airlines")
            onPricesUpdated("2000000")

            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onAirportNameUpdated(ItineraryType.DEPARTURE,"Tan Son Nhat")
            onAirportCityUpdated(ItineraryType.DEPARTURE, "Ho Chi Minh City")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 10)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))

            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onAirportNameUpdated(ItineraryType.ARRIVAL,"Changi")
            onAirportCityUpdated(ItineraryType.ARRIVAL, "Singapore")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 15)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditFlightUiState>()
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
    fun onSaveClick_whenCreateNewFlight_errorFromRepository_verifyShowError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        mockDateTimeFormatter()
        setupViewModel()

        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirlinesUpdated("Vietnam Airlines")
            onPricesUpdated("2000000")

            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onAirportNameUpdated(ItineraryType.DEPARTURE,"Tan Son Nhat")
            onAirportCityUpdated(ItineraryType.DEPARTURE, "Ho Chi Minh City")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 10)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))

            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onAirportNameUpdated(ItineraryType.ARRIVAL,"Changi")
            onAirportCityUpdated(ItineraryType.ARRIVAL, "Singapore")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 15)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        fakeTripDetailRepository.forceError = true
        advanceUntilIdle()

        // When
        val resultList = mutableListOf<AddEditFlightUiState>()
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
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_FLIGHT_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    @Test
    fun onSaveClick_whenUpdateFlightInfo_errorFromRepository_verifyShowError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        mockDateTimeFormatter()
        createDefaultFlightInfoInRepositoryForTesting()
        fakeTripDetailRepository.forceError = true
        setupViewModel(1L)

        // load trip info and update data on UI
        advanceUntilIdle()

        // When
        viewModel.apply {
            onFlightNumberUpdated("VN 393")
            onAirlinesUpdated("Vietnam Airlines")
            onPricesUpdated("2000000")

            onAirportCodeUpdated(ItineraryType.DEPARTURE, "SGN")
            onAirportNameUpdated(ItineraryType.DEPARTURE,"Tan Son Nhat")
            onAirportCityUpdated(ItineraryType.DEPARTURE, "Ho Chi Minh City")
            onFlightDateUpdated(ItineraryType.DEPARTURE, 10)
            onFlightTimeUpdated(ItineraryType.DEPARTURE, Pair(1, 2))

            onAirportCodeUpdated(ItineraryType.ARRIVAL, "SIN")
            onAirportNameUpdated(ItineraryType.ARRIVAL,"Changi")
            onAirportCityUpdated(ItineraryType.ARRIVAL, "Singapore")
            onFlightDateUpdated(ItineraryType.ARRIVAL, 15)
            onFlightTimeUpdated(ItineraryType.ARRIVAL, Pair(4, 5))
        }

        // Input new info for activity info
        advanceUntilIdle()

        val resultList = mutableListOf<AddEditFlightUiState>()
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
        Truth.assertThat(resultList[2].showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_FLIGHT_INFO)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }
}