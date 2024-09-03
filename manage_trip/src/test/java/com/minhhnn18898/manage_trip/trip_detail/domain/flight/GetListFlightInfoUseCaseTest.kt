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
class GetListFlightInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var getListFlightInfoUseCase: GetListFlightInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        getListFlightInfoUseCase = GetListFlightInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    private val firstFlightInfoTestData = Triple(
        FlightInfo(
            flightId = 1L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
            price = 2_000_000
        ),
        AirportInfo(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        ),
        AirportInfo(
            code = "SIN",
            city = "Singapore",
            airportName = "Changi"
        )
    )

    private val secondFlightInfoTestData = Triple(
        FlightInfo(
            flightId = 2L,
            flightNumber = "TW 256",
            operatedAirlines = "Thai Airways",
            departureTime = 2_000_000,
            arrivalTime = 2_500_000,
            price = 2_100_000
        ),
        AirportInfo(
            code = "BKK",
            city = "Bangkok",
            airportName = "Suvarnabhumi Airport"
        ),
        AirportInfo(
            code = "HAN",
            city = "Ha Noi",
            airportName = "Noi Bai"
        )
    )

    @Test
    fun getExistedFlightInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add some valid flight info so that it can be retrieved later
        fakeTripDetailRepository.addFlightAirportInfo(
            tripId = 1L,
            flightInfo = firstFlightInfoTestData.first,
            departAirport = firstFlightInfoTestData.second,
            destinationAirport = firstFlightInfoTestData.third
        )

        fakeTripDetailRepository.addFlightAirportInfo(
            tripId = 1L,
            flightInfo = secondFlightInfoTestData.first,
            departAirport = secondFlightInfoTestData.second,
            destinationAirport = secondFlightInfoTestData.third
        )

        // When - 1: get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<List<FlightWithAirportInfo>>>>()
        val dataResult = mutableListOf<List<FlightWithAirportInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListFlightInfoUseCase.execute(GetListFlightInfoUseCase.Param(tripId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then - 1
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)
        assertFlightAndAirportEqual(
            listExpected = mutableListOf(
                FlightWithAirportInfo(
                    flightInfo = firstFlightInfoTestData.first,
                    departAirport = firstFlightInfoTestData.second,
                    destinationAirport = firstFlightInfoTestData.third
                ),
                FlightWithAirportInfo(
                    flightInfo = secondFlightInfoTestData.first,
                    departAirport = secondFlightInfoTestData.second,
                    destinationAirport = secondFlightInfoTestData.third
                )
            ),
            listTarget = dataResult[0]
        )

        // When - 2: update new value
        val flightInfoUpdated = FlightInfo(
            flightId = 2L,
            flightNumber = "VN 191",
            operatedAirlines = "Vietnam Airlines - new",
            departureTime = 1_200_000,
            arrivalTime = 1_500_000,
            price = 2_300_000
        )
        val departAirportUpdated = AirportInfo(
            code = "BKK",
            city = "Bangkok City",
            airportName = "Suvarnabhumi Airport - Thailand"
        )

        val destinationAirportUpdated = AirportInfo(
            code = "HAN",
            city = "Ha Noi City",
            airportName = "Noi Bai Airport"
        )
        fakeTripDetailRepository.updateFlightAirportInfo(
            tripId = 1L,
            flightInfo = flightInfoUpdated,
            departAirport = departAirportUpdated,
            destinationAirport = destinationAirportUpdated
        )

        // Then - 2: verify new value is collected
        assertFlightAndAirportEqual(
            listExpected = mutableListOf(
                FlightWithAirportInfo(
                    flightInfo = firstFlightInfoTestData.first,
                    departAirport = firstFlightInfoTestData.second,
                    destinationAirport = firstFlightInfoTestData.third
                ),
                FlightWithAirportInfo(
                    flightInfo = flightInfoUpdated,
                    departAirport = departAirportUpdated,
                    destinationAirport = destinationAirportUpdated
                )
            ),
            listTarget = dataResult[1]
        )
    }

    @Test
    fun getNonExistedTripInfo_returnEmptyList() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<List<FlightWithAirportInfo>>>>()
        val dataResult = mutableListOf<List<FlightWithAirportInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListFlightInfoUseCase.execute(GetListFlightInfoUseCase.Param(tripId = 1L))?.toList(useCaseResult)
            (useCaseResult[1] as Result.Success).data.toList(dataResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(dataResult[0]).isEmpty()
    }

    @Test
    fun geTripInfo_throwExceptionFromRepository_returnCorrectError() = runTest {
        // Given - add some valid flight info so that it can be retrieved later, but throw exception from repository
        fakeTripDetailRepository.apply {
            addFlightAirportInfo(
                tripId = 1L,
                flightInfo = firstFlightInfoTestData.first,
                departAirport = firstFlightInfoTestData.second,
                destinationAirport = firstFlightInfoTestData.third
            )
            addFlightAirportInfo(
                tripId = 1L,
                flightInfo = secondFlightInfoTestData.first,
                departAirport = secondFlightInfoTestData.second,
                destinationAirport = secondFlightInfoTestData.third
            )
            forceError = true
        }

        // When - get current data from repository
        val useCaseResult = mutableListOf<Result<Flow<List<FlightWithAirportInfo>>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getListFlightInfoUseCase.execute(GetListFlightInfoUseCase.Param(tripId = 1L))?.toList(useCaseResult)
        }

        // Then
        Truth.assertThat(useCaseResult).hasSize(2)
        Truth.assertThat(useCaseResult[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(useCaseResult[1]).isInstanceOf(Result.Error::class.java)
        val error = ((useCaseResult[1]) as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }
}