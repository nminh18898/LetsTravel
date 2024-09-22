package com.minhhnn18898.manage_trip.trip_detail.domain.flight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertFlightAndAirportEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("SpellCheckingInspection")
@ExperimentalCoroutinesApi
class UpdateFlightInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var updateFlightInfoUseCaseTest: UpdateFlightInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        updateFlightInfoUseCaseTest = UpdateFlightInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun updateValidFlightInfo_canRetrieveNewFlightInfo() = runTest {
        // Given - add valid flight info so that it can be updated later
        fakeTripDetailRepository.upsertFlightInfo(
            tripId = 1L,
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
        )

        // When
        val flightInfoUpdated = FlightInfo(
            flightId = 1L,
            flightNumber = "VN 191",
            operatedAirlines = "Vietnam Airlines - new",
            departureTime = 1_200_000,
            arrivalTime = 1_500_000,
            price = 2_300_000
        )
        val departAirportUpdated = AirportInfo(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat International Airport"
        )

        val destinationAirportUpdated = AirportInfo(
            code = "SIN",
            city = "Singapore City",
            airportName = "Changi Airport"
        )

        val result = updateFlightInfoUseCaseTest.execute(
            UpdateFlightInfoUseCase.Param(
                tripId = 1L,
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)

        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(1L)
        )
    }

    @Test
    fun updatedAirportInfo_canRetrieveNewAirportInfoForOldFlight() = runTest {
        // Given - add valid flight info and airport so that it can be updated later
        // add flight for trip 1
        fakeTripDetailRepository.upsertFlightInfo(
            tripId = 1L,
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
                city = "Ho Chi Minh",
                airportName = "Tan Son Nhat"
            ),
            destinationAirport = AirportInfo(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi"
            )
        )

        // add flight for trip 2
        fakeTripDetailRepository.upsertFlightInfo(
            tripId = 2L,
            flightInfo = FlightInfo(
                flightId = 2L,
                flightNumber = "VN 797",
                operatedAirlines = "Jetstar Pacific",
                departureTime = 1_200_000,
                arrivalTime = 1_500_000,
                price = 2_300_000
            ),
            departAirport = AirportInfo(
                code = "SGN",
                city = "Ho Chi Minh",
                airportName = "Tan Son Nhat"
            ),
            destinationAirport = AirportInfo(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi"
            )
        )

        // When
        val flightInfoUpdated = FlightInfo(
            flightId = 2L,
            flightNumber = "VN 191",
            operatedAirlines = "Vietnam Airlines - new",
            departureTime = 1_200_000,
            arrivalTime = 1_500_000,
            price = 2_300_000
        )
        val departAirportUpdated = AirportInfo(
            code = "SGN", // same airport code but with different info
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat International Airport"
        )

        val destinationAirportUpdated = AirportInfo(
            code = "SIN", // same airport code but with different info
            city = "Singapore City",
            airportName = "Changi Airport"
        )

        val result = updateFlightInfoUseCaseTest.execute(
            UpdateFlightInfoUseCase.Param(
                tripId = 2L,
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Success::class.java)

        // verify second flight has new info
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(2L)
        )

        // verify first flight also has new airport info
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = FlightInfo(
                    flightId = 1L,
                    flightNumber = "VN 363",
                    operatedAirlines = "Vietnam Airlines",
                    departureTime = 1_000_000,
                    arrivalTime = 1_200_000,
                    price = 2_000_000
                ),
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(1L)
        )
    }

    @Test
    fun updateFlightInfo_dataNotExisted_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When
        val flightInfoUpdated = FlightInfo(
            flightId = 1L,
            flightNumber = "VN 191",
            operatedAirlines = "Vietnam Airlines - new",
            departureTime = 1_200_000,
            arrivalTime = 1_500_000,
            price = 2_300_000
        )
        val departAirportUpdated = AirportInfo(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat International Airport"
        )

        val destinationAirportUpdated = AirportInfo(
            code = "SIN",
            city = "Singapore City",
            airportName = "Changi Airport"
        )

        val result = updateFlightInfoUseCaseTest.execute(
            UpdateFlightInfoUseCase.Param(
                tripId = 1L,
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateFlightInfo::class.java)
    }

    @Test
    fun updateValidFlightInfo_throwExceptionUpdateFlightInfoFromRepository_canReturnError() = runTest {
        // Given - add valid flight info so that it can be updated later, but throw exception from repository
        fakeTripDetailRepository.upsertFlightInfo(
            tripId = 1L,
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
        )
        fakeTripDetailRepository.forceError = true

        // When
        val flightInfoUpdated = FlightInfo(
            flightId = 1L,
            flightNumber = "VN 191",
            operatedAirlines = "Vietnam Airlines - new",
            departureTime = 1_200_000,
            arrivalTime = 1_500_000,
            price = 2_300_000
        )
        val departAirportUpdated = AirportInfo(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat International Airport"
        )

        val destinationAirportUpdated = AirportInfo(
            code = "SIN",
            city = "Singapore City",
            airportName = "Changi Airport"
        )

        val result = updateFlightInfoUseCaseTest.execute(
            UpdateFlightInfoUseCase.Param(
                tripId = 1L,
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            )
        )?.toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result?.get(0)).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result?.get(1)).isInstanceOf(Result.Error::class.java)
        val error = (result?.get(1) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionUpdateFlightInfo::class.java)
    }
}