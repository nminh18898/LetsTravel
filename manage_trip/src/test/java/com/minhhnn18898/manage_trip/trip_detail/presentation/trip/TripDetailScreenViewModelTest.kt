package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetSortedListTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.data.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.data.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.domain.GetTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@Suppress("SpellCheckingInspection")
@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeTripInfoRepository: FakeTripInfoRepository

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    private lateinit var fakeCoverDefaultResourceProvider: FakeCoverDefaultResourceProvider

    private lateinit var fakeTripActivityDateSeparatorResourceProvider: FakeTripActivityDateSeparatorResourceProvider

    private lateinit var viewModel: TripDetailScreenViewModel

    private lateinit var mockAnnotations: AutoCloseable

    @Mock
    private lateinit var dateTimeFormatter: TripDetailDateTimeFormatter

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        fakeTripDetailRepository = FakeTripDetailRepository()
        fakeCoverDefaultResourceProvider = FakeCoverDefaultResourceProvider()
        fakeTripActivityDateSeparatorResourceProvider = FakeTripActivityDateSeparatorResourceProvider()

        mockAnnotations = MockitoAnnotations.openMocks(this)

        mockDateTimeFormatter()
    }

    private fun setupViewModel() {
        viewModel = TripDetailScreenViewModel(
            savedStateHandle = SavedStateHandle(mapOf(MainAppRoute.tripIdArg to 1L)),
            coverResourceProvider = fakeCoverDefaultResourceProvider,
            activityDateSeparatorResourceProvider = fakeTripActivityDateSeparatorResourceProvider,
            getTripInfoUseCase = GetTripInfoUseCase(fakeTripInfoRepository),
            getListFlightInfoUseCase = GetListFlightInfoUseCase(fakeTripDetailRepository),
            getListHotelInfoUseCase = GetListHotelInfoUseCase(fakeTripDetailRepository),
            getSortedListTripActivityInfoUseCase = GetSortedListTripActivityInfoUseCase(fakeTripDetailRepository),
            dateTimeFormatter = dateTimeFormatter
        )
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

        Mockito
            .`when`(dateTimeFormatter.getFormattedFlightDateTimeString(anyLong()))
            .thenAnswer {
                val dateParam = it.arguments[0] as Long
                dateParam.toString()
            }

        Mockito
            .`when`(dateTimeFormatter.findFlightDurationFormattedString(anyLong(), anyLong()))
            .thenAnswer {
                val from = it.arguments[0] as Long
                val to = it.arguments[1] as Long
                (to - from).toString()
            }
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
        fakeTripInfoRepository.reset()

        mockAnnotations.close()
    }

    @Test
    fun getTripId_getCorrectTripIdWhenInit() {
        // Given
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.tripId).isEqualTo(1L)
    }

    @Test
    fun getTripInfoContentState_initStateLoading() {
        // When
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.tripInfoContentState.value.isLoading).isTrue()
    }

    @Test
    fun getTripInfoContentState_hasTripInfo_withDefaultCover() = runTest {
        // Given
        fakeTripInfoRepository.addTrip(
            TripInfo(
                tripId = 1L,
                title = "Vietnam",
                coverType = TripInfoModel.TRIP_COVER_TYPE_DEFAULT,
                defaultCoverId = 1,
                customCoverPath = ""
            )
        )

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.tripInfoContentState.first()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.tripDisplay?.tripName).isEqualTo("Vietnam")
        Truth.assertThat(uiState.tripDisplay?.coverDisplay).isEqualTo(TripDefaultCoverDisplay(1))
    }

    @Test
    fun getTripInfoContentState_hasTripInfo_withCustomCover() = runTest {
        // Given
        fakeTripInfoRepository.addTrip(
            TripInfo(
                tripId = 1L,
                title = "Vietnam - Sai Gon",
                coverType = TripInfoModel.TRIP_COVER_TYPE_CUSTOM,
                defaultCoverId = 0,
                customCoverPath = "/sdcard/something.jpg"
            )
        )

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.tripInfoContentState.first()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.tripDisplay?.tripName).isEqualTo("Vietnam - Sai Gon")
        Truth.assertThat(uiState.tripDisplay?.coverDisplay).isEqualTo(TripCustomCoverDisplay("/sdcard/something.jpg"))
    }

    @Test
    fun getTripInfoContentState_emptyTripInfo() = runTest {
        // Given
        fakeTripInfoRepository.reset()

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.tripInfoContentState.first()
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isNotFound).isTrue()
    }

    @Test
    fun getFlightInfoContentState_initStateLoading() {
        // When
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.flightInfoContentState.value).isInstanceOf(UiState.Loading::class.java)
    }

    @Test
    fun getFlightInfoContentState_hasFlightInfo() = runTest {
        // Given
        fakeTripDetailRepository.upsertFlightInfo(1L, flightInfoInput)

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.flightInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((uiState as UiState.Success).data).isEqualTo(flightInfoOutput)
    }

    @Test
    fun getFlightInfoContentState_hasEmptyInfo() = runTest {
        // When
        setupViewModel()

        // Then
        val uiState = viewModel.flightInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((uiState as UiState.Success).data).isEmpty()
    }

    @Test
    fun getHotelInfoContentState() {
    }

    @Test
    fun getActivityInfoContentState() {
    }

    @Test
    fun getBudgetDisplay() {
    }

    private val flightInfoInput = mutableListOf(
        FlightWithAirportInfo(
            flightInfo = FlightInfo(
                flightId = 1L,
                flightNumber = "VN 363",
                operatedAirlines = "Vietnam Airlines",
                departureTime = 1_000_000,
                arrivalTime = 1_200_000,
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

        FlightWithAirportInfo(
            flightInfo = FlightInfo(
                flightId = 2L,
                flightNumber = "SG 125",
                operatedAirlines = "Singapore Airlines",
                departureTime = 1_000_000,
                arrivalTime = 1_300_000,
                price = 2_500_000
            ),
            departAirport = AirportInfo(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi"
            ),
            destinationAirport = AirportInfo(
                code = "BKK",
                city = "Bangkok",
                airportName = "Suvarnabhumi"
            )
        ),

        FlightWithAirportInfo(
            flightInfo = FlightInfo(
                flightId = 3L,
                flightNumber = "TW 256",
                operatedAirlines = "Thai Airways",
                departureTime = 2_000_000,
                arrivalTime = 2_500_000,
                price = 2_100_000
            ),

            departAirport = AirportInfo(
                code = "BKK",
                city = "Bangkok",
                airportName = "Suvarnabhumi"
            ),

            destinationAirport = AirportInfo(
                code = "HAN",
                city = "Ha Noi",
                airportName = "Noi Bai"
            )
        )
    )

    private val flightInfoOutput = mutableListOf(
        FlightDisplayInfo(
            flightId = 1L,
            flightNumber = "VN 363",
            departAirport = AirportDisplayInfo(
                code = "SGN",
                airportName = "Tan Son Nhat",
                city = "Ho Chi Minh City"
            ),
            destinationAirport = AirportDisplayInfo(
                code = "SIN",
                airportName = "Changi",
                city = "Singapore"
            ),
            operatedAirlines = "Vietnam Airlines",
            departureTime = "1000000",
            arrivalTime = "1200000",
            duration = "200000",
            price = "2,000,000"
        ),

        FlightDisplayInfo(
            flightId = 2L,
            flightNumber = "SG 125",
            departAirport = AirportDisplayInfo(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi"
            ),
            destinationAirport = AirportDisplayInfo(
                code = "BKK",
                city = "Bangkok",
                airportName = "Suvarnabhumi"
            ),
            operatedAirlines = "Singapore Airlines",
            departureTime = "1000000",
            arrivalTime = "1300000",
            duration = "300000",
            price = "2,500,000"
        ),

        FlightDisplayInfo(
            flightId = 3L,
            flightNumber = "TW 256",
            departAirport = AirportDisplayInfo(
                code = "BKK",
                city = "Bangkok",
                airportName = "Suvarnabhumi"
            ),
            destinationAirport = AirportDisplayInfo(
                code = "HAN",
                city = "Ha Noi",
                airportName = "Noi Bai"
            ),
            operatedAirlines = "Thai Airways",
            departureTime = "2000000",
            arrivalTime = "2500000",
            duration = "500000",
            price = "2,100,000"
        )
    )
}