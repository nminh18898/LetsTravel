package com.minhhnn18898.manage_trip.trip_detail.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepositoryImpl
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@Suppress("SpellCheckingInspection")
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TripDetailRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var mockAnnotations: AutoCloseable

    private lateinit var tripInfoRepository: TripInfoRepository
    private lateinit var repository: TripDetailRepository
    private lateinit var database: UserTripDatabase

    @Mock
    private lateinit var dateTimeFormatter: TripDetailDateTimeFormatter

    @Before
    fun setup() {
        mockAnnotations = MockitoAnnotations.openMocks(this)

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserTripDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        // repository to create trip info
        tripInfoRepository  =
            TripInfoRepositoryImpl(
                tripInfoDao = database.tripInfoDao(),
                ioDispatcher = Dispatchers.Main
            )
        insertTripInfo()

        // repository under test
       repository =
            TripDetailRepository(
                ioDispatcher = Dispatchers.Main,
                airportInfoDao =  database.airportInfoDao(),
                flightInfoDao = database.flightInfoDao(),
                hotelInfoDao = database.hotelInfoDao(),
                activityInfoDao = database.activityInfoDao(),
                dateTimeFormatter = dateTimeFormatter
            )
    }

    private fun insertTripInfo() = runTest {
        tripInfoRepository.insertTripInfo(
            TripInfoModel(
                tripId = 0,
                title = "Vietnam",
                coverType = 1,
                defaultCoverId = 1,
                customCoverPath = "https://testing.com"
            )
        )
    }

    @After
    fun cleanup() {
        mockAnnotations.close()
        database.close()
    }

    @Test
    fun insertFlightInfo_retrieveFlightInfo() = runTest {
        // Given
        val flightInfo = FlightInfo(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
            price = 2_000_000
        )

        val departAirport = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        val destinationAirport = AirportInfoModel(
            code = "SIN",
            city = "Singapore",
            airportName = "Changi"
        )

        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = flightInfo,
            departAirport = departAirport,
            destinationAirport = destinationAirport
        )

        // When
        val result = repository.getFlightInfo(1L).first()

        // Then
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfo,
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
            target = result
        )
    }

    @Test
    fun updateFlightInfo_retrieveFlightInfo() = runTest {
        // Given
        val flightInfo = FlightInfo(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
            price = 2_000_000
        )

        val departAirport = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        val destinationAirport = AirportInfoModel(
            code = "SIN",
            city = "Singapore",
            airportName = "Changi"
        )

        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = flightInfo,
            departAirport = departAirport,
            destinationAirport = destinationAirport
        )

        // When
        val flightInfoUpdated = FlightInfo(
            flightId = 1L,
            flightNumber = "VN 797",
            operatedAirlines = "Jetstar Pacific",
            departureTime = 1_200_000,
            arrivalTime = 1_500_000,
            price = 2_300_000
        )

        val departAirportUpdated = AirportInfoModel(
            code = "HAN",
            city = "Ha Noi",
            airportName = "Noi Bai"
        )

        val destinationAirportUpdated = AirportInfoModel(
            code = "SIN",
            city = "Singapore City",
            airportName = "Changi Airport"
        )
        repository.updateFlightInfo(
            tripId = 1L,
            flightInfo = flightInfoUpdated,
            departAirport = departAirportUpdated,
            destinationAirport = destinationAirportUpdated
        )
        val result = repository.getFlightInfo(1L).first()

        // Then
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfoUpdated,
                departAirport = AirportInfo(
                    code = "HAN",
                    city = "Ha Noi",
                    airportName = "Noi Bai"
                ),
                destinationAirport = AirportInfo(
                    code = "SIN",
                    city = "Singapore City",
                    airportName = "Changi Airport"
                )
            ),
            target = result
        )
    }

    @Test
    fun deleteFlightInfo_retrieveFlightInfo() = runTest {
        // Given
        val flightInfo = FlightInfo(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
            price = 2_000_000
        )

        val departAirport = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        val destinationAirport = AirportInfoModel(
            code = "SIN",
            city = "Singapore",
            airportName = "Changi"
        )

        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = flightInfo,
            departAirport = departAirport,
            destinationAirport = destinationAirport
        )

        // When
        repository.deleteFlightInfo(1L)
        val result = repository.getFlightInfo(1L).first()

        // Then
        Truth.assertThat(result).isNull()
    }

    @Test
    fun getListFlightInfo_retrieveSortedByFlightTime() = runTest {
        // Given
        // insert the first flight
        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = FlightInfo(
                flightId = 0L,
                flightNumber = "VN 363",
                operatedAirlines = "Vietnam Airlines",
                departureTime = 1_500_000,
                arrivalTime = 1_800_000,
                price = 2_000_000
            ),
            departAirport = AirportInfoModel(
                code = "SGN",
                city = "Ho Chi Minh City",
                airportName = "Tan Son Nhat"
            ),
            destinationAirport = AirportInfoModel(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi Airport"
            )
        )

        // insert the second flight
        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = FlightInfo(
                flightId = 0L,
                flightNumber = "SG 125",
                operatedAirlines = "Singapore Airlines",
                departureTime = 1_000_000,
                arrivalTime = 1_300_000,
                price = 2_500_000
            ),
            departAirport = AirportInfoModel(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi Airport"
            ),
            destinationAirport = AirportInfoModel(
                code = "BKK",
                city = "Bangkok",
                airportName = "Suvarnabhumi Airport"
            )
        )

        // insert the third flight
        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = FlightInfo(
                flightId = 0L,
                flightNumber = "TW 256",
                operatedAirlines = "Thai Airways",
                departureTime = 2_000_000,
                arrivalTime = 2_500_000,
                price = 2_100_000
            ),
            departAirport = AirportInfoModel(
                code = "BKK",
                city = "Bangkok",
                airportName = "Suvarnabhumi Airport"
            ),
            destinationAirport = AirportInfoModel(
                code = "HAN",
                city = "Ha Noi",
                airportName = "Noi Bai"
            )
        )

        // When
        val result = repository.getListFlightInfo(1L).first()

        // Then
        Truth.assertThat(result).hasSize(3)
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = FlightInfo(
                    flightId = 0L,
                    flightNumber = "SG 125",
                    operatedAirlines = "Singapore Airlines",
                    departureTime = 1_000_000,
                    arrivalTime = 1_300_000,
                    price = 2_500_000
                ),
                departAirport = AirportInfo(
                    code = "SIN",
                    city = "Singapore",
                    airportName = "Changi Airport"
                ),
                destinationAirport = AirportInfo(
                    code = "BKK",
                    city = "Bangkok",
                    airportName = "Suvarnabhumi Airport"
                )
            ),
            target = result[0]
        )
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = FlightInfo(
                    flightId = 0L,
                    flightNumber = "VN 363",
                    operatedAirlines = "Vietnam Airlines",
                    departureTime = 1_500_000,
                    arrivalTime = 1_800_000,
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
                    airportName = "Changi Airport"
                )
            ),
            target = result[1]
        )
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = FlightInfo(
                    flightId = 0L,
                    flightNumber = "TW 256",
                    operatedAirlines = "Thai Airways",
                    departureTime = 2_000_000,
                    arrivalTime = 2_500_000,
                    price = 2_100_000
                ),
                departAirport = AirportInfo(
                    code = "BKK",
                    city = "Bangkok",
                    airportName = "Suvarnabhumi Airport"
                ),
                destinationAirport = AirportInfo(
                    code = "HAN",
                    city = "Ha Noi",
                    airportName = "Noi Bai"
                )
            ),
            target = result[2]
        )
    }

    @Test
    fun getFlightInfo() = runTest {
        // Given
        val flightInfo = FlightInfo(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
            price = 2_000_000
        )

        val departAirport = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        val destinationAirport = AirportInfoModel(
            code = "SIN",
            city = "Singapore",
            airportName = "Changi"
        )

        repository.insertFlightInfo(
            tripId = 1L,
            flightInfo = flightInfo,
            departAirport = departAirport,
            destinationAirport = destinationAirport
        )

        // When
        val result = repository.getFlightInfo(1L).first()

        // Then
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfo,
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
            target = result
        )
    }

    @Test
    fun insertHotelInfo_retrieveHotelInfo() = runTest {
        // Given
        val hotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = hotelInfo
        )

        // When
        val result = repository.getHotelInfo(1L).first()

        // Then
        assertHotelInfo(
            expected = hotelInfo,
            target = result
        )
    }

    @Test
    fun updateHotelInfo_retrieveHotelInfo() = runTest {
        // Given
        val hotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = hotelInfo
        )

        // When
        val hotelInfoUpdated = HotelInfo(
            hotelId = 1L,
            hotelName =  "Eastin Grand Hotel",
            address =  "Phu Nhuan District, Ho Chi Minh City",
            price = 3_000_000,
            checkInDate =  2_000_000,
            checkOutDate = 2_200_000
        )
        repository.updateHotelInfo(
            tripId = 1L,
            hotelInfo = hotelInfoUpdated
        )
        val result = repository.getHotelInfo(1L).first()

        // Then
        assertHotelInfo(
            expected = hotelInfoUpdated,
            target = result
        )
    }

    @Test
    fun getAllHotelInfo_retrieveSortedHotelInfoByCheckinDate() = runTest {
        // Given
        val firstHotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            price = 2_200_000,
            checkInDate = 1_500_000,
            checkOutDate = 1_800_000,
        )
        val secondHotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName =  "Eastin Grand Hotel",
            address =  "Phu Nhuan District, Ho Chi Minh City",
            price = 3_000_000,
            checkInDate =  1_000_000,
            checkOutDate = 1_200_000
        )
        val thirdHotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName =  "Holiday Inn & Suites Saigon Airport",
            address =  "18E Cong Hoa, Ho Chi Minh City",
            price = 2_700_000,
            checkInDate =  2_000_000,
            checkOutDate = 2_500_000
        )

        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = firstHotelInfo
        )

        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = secondHotelInfo
        )

        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = thirdHotelInfo
        )

        // When
        val result = repository.getAllHotelInfo(1L).first()

        // Then
        Truth.assertThat(result).hasSize(3)
        assertHotelInfo(
            expected = secondHotelInfo,
            target = result[0]
        )
        assertHotelInfo(
            expected = firstHotelInfo,
            target = result[1]
        )
        assertHotelInfo(
            expected = thirdHotelInfo,
            target = result[2]
        )
    }

    @Test
    fun getHotelInfo() = runTest {
        // Given
        val hotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = hotelInfo
        )

        // When
        val result = repository.getHotelInfo(1L).first()

        // Then
        assertHotelInfo(
            expected = hotelInfo,
            target = result
        )
    }

    @Test
    fun deleteHotelInfo_retrieveHotelInfo() = runTest {
        // Given
        val hotelInfo = HotelInfo(
            hotelId = 0L,
            hotelName = "Liberty Central Riverside Hotel",
            address = "District 1, Ho Chi Minh City",
            checkInDate = 1_000_000,
            checkOutDate = 1_200_000,
            price = 2_200_000
        )
        repository.insertHotelInfo(
            tripId = 1L,
            hotelInfo = hotelInfo
        )

        // When
        repository.deleteHotelInfo(1L)
        val result = repository.getHotelInfo(1L).first()

        // Then
        Truth.assertThat(result).isNull()
    }

    @Test
    fun insertActivityInfo_retrieveActivityInfo() = runTest {
        // Given
        val activityInfo = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000
        )

        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = activityInfo
        )

        // When
        val result = repository.getActivityInfo(1L).first()

        // Then
        assertActivityInfo(activityInfo, result)
    }

    @Test
    fun updateActivityInfo_retrieveActivityInfo() = runTest {
        // Given
        val activityInfo = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000
        )

        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = activityInfo
        )

        // When
        val activityInfoUpdated = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000
        )
        repository.updateActivityInfo(1L, activityInfoUpdated)
        val result = repository.getActivityInfo(1L).first()

        // Then
        assertActivityInfo(activityInfoUpdated, result)
    }

    @Test
    fun deleteActivityInfo_retrieveActivityInfo() = runTest {
        // Given
        val activityInfo = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000
        )

        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = activityInfo
        )

        // When
        repository.deleteActivityInfo(1L)
        val result = repository.getActivityInfo(1L).first()

        // Then
        Truth.assertThat(result).isNull()
    }

    @Test
    fun getSortedActivityInfoByStartTime() = runTest {
        // Given
        val firstActivity = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_500_000,
            timeTo = 1_800_000,
            price = 2_000_000
        )
        val secondActivity = TripActivityInfo(
            activityId = 0L,
            title = "Saigon River Tour in Ho Chi Minh",
            description = "Hop on board and join us on a luxury cruise on the Saigon River in the city center of Ho Chi Minh City",
            photo = "https://testing.com/photo2",
            timeFrom = 1_200_000,
            timeTo = 1_400_000,
            price = 2_900_000
        )
        val thirdActivity = TripActivityInfo(
            activityId = 0L,
            title = "Cu Chi Tunnels Tour",
            description = "Used by the Viet Cong during the Vietnam War, the Cu Chi Tunnels are a network of underground tunnels stretching more than 124 miles (200 kilometers)",
            photo = "https://testing.com/photo3",
            timeFrom = 2_200_000,
            timeTo = 2_300_000,
            price = 2_200_000
        )

        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = firstActivity
        )
        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = secondActivity
        )
        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = thirdActivity
        )

        Mockito
            .`when`(dateTimeFormatter.getStartOfTheDay(anyLong()))
            .thenAnswer {
                val param = it.arguments[0] as Long
                (param / 1_000_000f).toInt() * 1_000_000L
            }

        // When
        val result = repository.getSortedActivityInfo(1L).first()

        // Then
        Truth.assertThat(result).hasSize(2)

        // list activity that map by the start of day
        val firstDateActivities = result[1_000_000]
        val secondDateActivities = result[2_000_000]

        // expecte: first date has two activities, and the second day has one
        Truth.assertThat(firstDateActivities).hasSize(2)
        Truth.assertThat(secondDateActivities).hasSize(1)

        assertActivityInfo(secondActivity, firstDateActivities?.get(0))
        assertActivityInfo(firstActivity, firstDateActivities?.get(1))
        assertActivityInfo(thirdActivity, secondDateActivities?.get(0))
    }

    @Test
    fun getActivityInfo() = runTest {
        // Given
        val activityInfo = TripActivityInfo(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
        )

        repository.insertActivityInfo(
            tripId = 1L,
            activityInfo = activityInfo
        )

        // When
        val result = repository.getActivityInfo(1L).first()

        // Then
        assertActivityInfo(activityInfo, result)
    }

    private fun assertFlightAndAirportEqual(expected: FlightWithAirportInfo, target: FlightWithAirportInfo?) {
        Truth.assertThat(target).isNotNull()
        assertFlightInfo(expected.flightInfo, target?.flightInfo)
        assertAirportInfo(expected.departAirport, target?.departAirport)
        assertAirportInfo(expected.destinationAirport, target?.destinationAirport)
    }

    private fun assertFlightInfo(expected: FlightInfo, target: FlightInfo?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.flightNumber).isEqualTo(expected.flightNumber)
        Truth.assertThat(target?.operatedAirlines).isEqualTo(expected.operatedAirlines)
        Truth.assertThat(target?.departureTime).isEqualTo(expected.departureTime)
        Truth.assertThat(target?.arrivalTime).isEqualTo(expected.arrivalTime)
        Truth.assertThat(target?.price).isEqualTo(expected.price)
    }

    private fun assertAirportInfo(expected: AirportInfo, target: AirportInfo?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.code).isEqualTo(expected.code)
        Truth.assertThat(target?.city).isEqualTo(expected.city)
        Truth.assertThat(target?.airportName).isEqualTo(expected.airportName)
    }

    private fun assertHotelInfo(expected: HotelInfo, target: HotelInfo?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.hotelName).isEqualTo(expected.hotelName)
        Truth.assertThat(target?.address).isEqualTo(expected.address)
        Truth.assertThat(target?.checkInDate).isEqualTo(expected.checkInDate)
        Truth.assertThat(target?.checkOutDate).isEqualTo(expected.checkOutDate)
        Truth.assertThat(target?.price).isEqualTo(expected.price)
    }

    private fun assertActivityInfo(expected: TripActivityInfo, target: TripActivityInfo?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.title).isEqualTo(expected.title)
        Truth.assertThat(target?.description).isEqualTo(expected.description)
        Truth.assertThat(target?.photo).isEqualTo(expected.photo)
        Truth.assertThat(target?.timeFrom).isEqualTo(expected.timeFrom)
        Truth.assertThat(target?.timeTo).isEqualTo(expected.timeTo)
        Truth.assertThat(target?.price).isEqualTo(expected.price)
    }
}