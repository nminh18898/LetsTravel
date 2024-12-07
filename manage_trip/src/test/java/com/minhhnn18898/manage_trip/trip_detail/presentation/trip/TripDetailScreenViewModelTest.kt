package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.manage_trip.test_helper.FakeTripActivityDateSeparatorResourceProvider
import com.minhhnn18898.manage_trip.test_helper.FakeTripDetailDateTimeFormatter
import com.minhhnn18898.manage_trip.test_helper.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetSortedListTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.manage_trip.test_helper.FakeCoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.test_helper.FakeTripInfoRepository
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.AirportDisplayInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.BudgetPortion
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.BudgetType
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.FlightDisplayInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.HotelDisplayInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.TripActivityDateGroupHeader
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.TripActivityDisplayInfo
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

    private lateinit var fakeDateTimeFormatter: FakeTripDetailDateTimeFormatter

    @Before
    fun setup() {
        fakeTripInfoRepository = FakeTripInfoRepository()
        fakeTripDetailRepository = FakeTripDetailRepository()
        fakeCoverDefaultResourceProvider = FakeCoverDefaultResourceProvider()
        fakeTripActivityDateSeparatorResourceProvider = FakeTripActivityDateSeparatorResourceProvider()
        fakeDateTimeFormatter = FakeTripDetailDateTimeFormatter()
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
            dateTimeFormatter = fakeDateTimeFormatter
        )
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
        fakeTripInfoRepository.reset()
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
    fun getFlightInfoContentState_hasFlightInfo_flightLoaded() = runTest {
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
    fun getFlightInfoContentState_hasErrorWhenLoad() = runTest {
        // Given
        fakeTripDetailRepository.forceError = true

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.flightInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Error::class.java)
    }

    @Test
    fun getFlightInfoContentState_hasFlightInfo_budgetUpdated() = runTest {
        // Given
        fakeTripDetailRepository.upsertFlightInfo(1L, flightInfoInput)

        // When
        setupViewModel()
        viewModel.flightInfoContentState.first()

        // Then
        Truth.assertThat(viewModel.budgetDisplay.total).isEqualTo( 6_600_000)
        Truth.assertThat(viewModel.budgetDisplay.portions).isEqualTo(
            listOf(
                BudgetPortion(
                    type = BudgetType.FLIGHT,
                    price = 6_600_000
                )
            )
        )
    }

    @Test
    fun getHotelInfoContentState_initStateLoading() {
        // When
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.flightInfoContentState.value).isInstanceOf(UiState.Loading::class.java)
    }

    @Test
    fun getHotelInfoContentState_hasHotelInfo_hotelLoaded() = runTest {
        // Given
        fakeTripDetailRepository.upsertHotelInfo(1L, *hotelInfoInput.toTypedArray())

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.hotelInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((uiState as UiState.Success).data).isEqualTo(hotelInfoOutput)
    }

    @Test
    fun getHotelInfoContentState_hasEmptyInfo() = runTest {
        // When
        setupViewModel()

        // Then
        val uiState = viewModel.hotelInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((uiState as UiState.Success).data).isEmpty()
    }

    @Test
    fun getHotelInfoContentState_hasErrorWhenLoad() = runTest {
        // Given
        fakeTripDetailRepository.forceError = true

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.hotelInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Error::class.java)
    }

    @Test
    fun getHotelInfoContentState_hasHotelInfo_budgetUpdated() = runTest {
        // Given
        fakeTripDetailRepository.upsertHotelInfo(1L, *hotelInfoInput.toTypedArray())

        // When
        setupViewModel()
        viewModel.hotelInfoContentState.first()

        // Then
        Truth.assertThat(viewModel.budgetDisplay.total).isEqualTo( 7_900_000)
        Truth.assertThat(viewModel.budgetDisplay.portions).isEqualTo(
            listOf(
                BudgetPortion(
                    type = BudgetType.HOTEL,
                    price = 7_900_000
                )
            )
        )
    }

    @Test
    fun getActivityInfoContentState_initStateLoading() {
        // When
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.activityInfoContentState.value).isInstanceOf(UiState.Loading::class.java)
    }

    @Test
    fun getActivityInfoContentState_hasActivityInfo_activityLoaded() = runTest {
        // Given
        fakeTripDetailRepository.upsertActivityInfo(1L, *activityInfoInput.toTypedArray())

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.activityInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((uiState as UiState.Success).data).isEqualTo(activityInfoOutput)
    }

    @Test
    fun getActivityInfoContentState_hasEmptyInfo() = runTest {
        // When
        setupViewModel()

        // Then
        val uiState = viewModel.activityInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((uiState as UiState.Success).data).isEmpty()
    }

    @Test
    fun getActivityInfoContentState_hasErrorWhenLoad() = runTest {
        // Given
        fakeTripDetailRepository.forceError = true

        // When
        setupViewModel()

        // Then
        val uiState = viewModel.activityInfoContentState.first()
        Truth.assertThat(uiState).isInstanceOf(UiState.Error::class.java)
    }

    @Test
    fun getActivityInfoContentState_hasActivityInfo_budgetUpdated() = runTest {
        // Given
        fakeTripDetailRepository.upsertActivityInfo(1L, *activityInfoInput.toTypedArray())

        // When
        setupViewModel()
        viewModel.activityInfoContentState.first()

        // Then
        Truth.assertThat(viewModel.budgetDisplay.total).isEqualTo( 7_100_000)
        Truth.assertThat(viewModel.budgetDisplay.portions).isEqualTo(
            listOf(
                BudgetPortion(
                    type = BudgetType.ACTIVITY,
                    price = 7_100_000
                )
            )
        )
    }

    @Test
    fun getBudgetDisplay_returnCorrectValue() = runTest {
        // Given
        setupViewModel()

        fakeTripDetailRepository.upsertFlightInfo(1L, flightInfoInput)
        fakeTripDetailRepository.upsertHotelInfo(1L, *hotelInfoInput.toTypedArray())
        fakeTripDetailRepository.upsertActivityInfo(1L, *activityInfoInput.toTypedArray())

        // When
        viewModel.flightInfoContentState.first()
        viewModel.hotelInfoContentState.first()
        viewModel.activityInfoContentState.first()

        // Then
        Truth.assertThat(viewModel.budgetDisplay.total).isEqualTo( 21_600_000)
        Truth.assertThat(viewModel.budgetDisplay.portions).containsExactlyElementsIn(
            listOf(
                BudgetPortion(
                    type = BudgetType.HOTEL,
                    price = 7_900_000
                ),

                BudgetPortion(
                    type = BudgetType.FLIGHT,
                    price = 6_600_000
                ),

                BudgetPortion(
                    type = BudgetType.ACTIVITY,
                    price = 7_100_000
                )
            )
        )
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

    private val hotelInfoInput = mutableListOf(
        HotelInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            price = 2_200_000,
            checkInDate = 1_500_000,
            checkOutDate = 1_800_000,
        ),

        HotelInfo(
            hotelId = 2L,
            hotelName = "Eastin Grand Hotel",
            address = "Phu Nhuan District, Ho Chi Minh City",
            price = 3_000_000,
            checkInDate =  1_000_000,
            checkOutDate = 1_200_000
        ),

        HotelInfo(
            hotelId = 3L,
            hotelName = "Holiday Inn & Suites Saigon Airport",
            address = "18E Cong Hoa, Ho Chi Minh City",
            price = 2_700_000,
            checkInDate =  2_000_000,
            checkOutDate = 2_500_000
        )
    )

    private val hotelInfoOutput = mutableListOf(
        HotelDisplayInfo(
            hotelId = 1L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = "1500000",
            checkOutDate = "1800000",
            duration = 300000,
            price = "2,200,000"
        ),

        HotelDisplayInfo(
            hotelId = 2L,
            hotelName = "Eastin Grand Hotel",
            address = "Phu Nhuan District, Ho Chi Minh City",
            checkInDate = "1000000",
            checkOutDate = "1200000",
            duration = 200000,
            price = "3,000,000"
        ),

        HotelDisplayInfo(
            hotelId = 3L,
            hotelName = "Holiday Inn & Suites Saigon Airport",
            address = "18E Cong Hoa, Ho Chi Minh City",
            checkInDate = "2000000",
            checkOutDate = "2500000",
            duration = 500000,
            price = "2,700,000"
        )
    )

    private val activityInfoInput = mutableListOf(
        TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_500_000,
            timeTo = 1_800_000,
            price = 2_000_000
        ),

        TripActivityInfo(
            activityId = 2L,
            title = "Saigon River Tour in Ho Chi Minh",
            description = "Hop on board and join us on a luxury cruise on the Saigon River in the city center of Ho Chi Minh City",
            photo = "https://testing.com/photo2",
            timeFrom = 1_200_000,
            timeTo = 1_400_000,
            price = 2_900_000
        ),

        TripActivityInfo(
            activityId = 3L,
            title = "Cu Chi Tunnels Tour",
            description = "Used by the Viet Cong during the Vietnam War, the Cu Chi Tunnels are a network of underground tunnels stretching more than 124 miles (200 kilometers)",
            photo = "https://testing.com/photo3",
            timeFrom = 2_200_000,
            timeTo = 2_300_000,
            price = 2_200_000
        )
    )

    private val activityInfoOutput = mutableListOf(
        TripActivityDateGroupHeader(
            title = "1000000",
            dateOrdering = 1,
            resId = 1
        ),

        TripActivityDisplayInfo(
            activityId = 2L,
            title = "Saigon River Tour in Ho Chi Minh",
            photo = "https://testing.com/photo2",
            description = "Hop on board and join us on a luxury cruise on the Saigon River in the city center of Ho Chi Minh City",
            date = "1200000",
            startTime = "1200000",
            endTime = "1400000",
            price = "2,900,000"
        ),

        TripActivityDisplayInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms",
            photo = "https://testing.com/photo",
            description = "Mekong Delta Tour from HCM City",
            date = "1500000",
            startTime = "1500000",
            endTime = "1800000",
            price = "2,000,000"
        ),

        TripActivityDateGroupHeader(
            title = "2000000",
            dateOrdering = 2,
            resId = 2
        ),

        TripActivityDisplayInfo(
            activityId = 3L,
            title = "Cu Chi Tunnels Tour",
            photo = "https://testing.com/photo3",
            description = "Used by the Viet Cong during the Vietnam War, the Cu Chi Tunnels are a network of underground tunnels stretching more than 124 miles (200 kilometers)",
            date = "2200000",
            startTime = "2200000",
            endTime = "2300000",
            price = "2,200,000"
        )
    )
}