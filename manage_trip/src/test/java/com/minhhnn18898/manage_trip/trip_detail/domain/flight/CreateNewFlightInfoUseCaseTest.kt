package com.minhhnn18898.manage_trip.trip_detail.domain.flight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.test_helper.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertFlightInfo
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
class CreateNewFlightInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var createFlightInfoUseCase: CreateNewFlightInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        createFlightInfoUseCase = CreateNewFlightInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun insertValidFlightInfo_canRetrieveTheSameFlightInfo() = runTest {
        // When
        val flightInfo = FlightInfo(
            flightId = 0L,
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

        val result = createFlightInfoUseCase.execute(
            CreateNewFlightInfoUseCase.Param(
                tripId = 1L,
                flightInfo = flightInfo,
                departAirport = departAirport,
                destinationAirport = destinationAirport
            )
        ).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        val id = (result[1] as Result.Success).data

        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfo.copy(flightId = id),
                departAirport = departAirport,
                destinationAirport = destinationAirport
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(id)
        )
    }

    @Test
    fun insertValidFlightInfo_withAirportInfoAlreadyExisted_canRetrieveFlightInfoWithNewAirportInfo() = runTest {
        // Given
        fakeTripDetailRepository.apply {
            upsertAirportInfo(
                AirportInfo(
                    code = "SGN",
                    city = "Sai Gon",
                    airportName = "Tan Son Nhat"
                )
            )
            upsertAirportInfo(
                AirportInfo(
                    code = "SIN",
                    city = "Singapore",
                    airportName = "Changi"
                )
            )
        }

        // When
        val flightInfo = FlightInfo(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_200_000,
            price = 2_000_000
        )

        val departAirport = AirportInfo(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat International Airport"
        )

        val destinationAirport = AirportInfo(
            code = "SIN",
            city = "Singapore City",
            airportName = "Changi Airport"
        )

        val result = createFlightInfoUseCase.execute(
            CreateNewFlightInfoUseCase.Param(
                tripId = 1L,
                flightInfo = flightInfo,
                departAirport = departAirport,
                destinationAirport = destinationAirport
            )
        ).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        val id = (result[1] as Result.Success).data

        assertFlightAndAirportEqual(
            expected = FlightWithAirportInfo(
                flightInfo = flightInfo.copy(flightId = id),
                departAirport = departAirport,
                destinationAirport = destinationAirport
            ),
            target = fakeTripDetailRepository.getFlightAirportInfo(id)
        )
    }

    @Test
    fun insertValidFlightInfo_throwExceptionFromRepository_canReturnError() = runTest {
        // Given
        fakeTripDetailRepository.forceError = true

        // When
        val flightInfo = FlightInfo(
            flightId = 0L,
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

        val result = createFlightInfoUseCase.execute(
            CreateNewFlightInfoUseCase.Param(
                tripId = 1L,
                flightInfo = flightInfo,
                departAirport = departAirport,
                destinationAirport = destinationAirport
            )
        ).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionInsertFlightInfo::class.java)
    }
}