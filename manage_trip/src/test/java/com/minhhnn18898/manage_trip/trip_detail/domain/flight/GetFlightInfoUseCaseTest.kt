package com.minhhnn18898.manage_trip.trip_detail.domain.flight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertFlightAndAirportEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("SpellCheckingInspection")
@ExperimentalCoroutinesApi
class GetFlightInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var getFlightInfoUseCase: GetFlightInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        getFlightInfoUseCase = GetFlightInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun getExistedFlightInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add valid flight info so that it can be retrieved later
        val flightInfo = FlightInfo(
            flightId = 1L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
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

        fakeTripDetailRepository.addFlightAirportInfo(
            tripId = 1L,
            flightInfo = flightInfo,
            departAirport = departAirport,
            destinationAirport = destinationAirport
        )

        // When - 1: get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<FlightWithAirportInfo?>>>()
        val dataResult = mutableListOf<FlightWithAirportInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getFlightInfoUseCase.execute(GetFlightInfoUseCase.Param(flightId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then - 1
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)

        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfo,
                departAirport = departAirport,
                destinationAirport = destinationAirport
            ),
            target = dataResult[0]
        )

        // When - 2: update new value
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
        fakeTripDetailRepository.updateFlightAirportInfo(
            tripId = 1L,
            flightInfo = flightInfoUpdated,
            departAirport = departAirportUpdated,
            destinationAirport = destinationAirportUpdated
        )

        // Then - 2: verify new value is collected
        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfoUpdated,
                departAirport = departAirportUpdated,
                destinationAirport = destinationAirportUpdated
            ),
            target = dataResult[1]
        )
    }

    @Test
    fun getNonExistedFlightInfo_returnNullValue() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<FlightWithAirportInfo?>>>()
        val dataResult = mutableListOf<FlightWithAirportInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getFlightInfoUseCase.execute(GetFlightInfoUseCase.Param(flightId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(dataResult[0]).isNull()
    }

    @Test
    fun getValidFlightInfo_throwExceptionFromRepository_returnCorrectError_returnNullValue() = runTest {
        // Given -  add valid flight info so that it can be retrieved, but throw exception from repository
        fakeTripDetailRepository.addFlightAirportInfo(
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

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<FlightWithAirportInfo?>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getFlightInfoUseCase.execute(GetFlightInfoUseCase.Param(flightId = 1L))?.toList(useCaseResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Error::class.java)
        val error = ((useCaseResult[1]) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }
}