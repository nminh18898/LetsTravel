package com.minhhnn18898.manage_trip.trip_detail.domain.flight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.ExceptionDeleteFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.flight.DeleteFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.utils.assertAirportInfoEqual
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
class DeleteFlightInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var deleteFlightInfoUseCase: DeleteFlightInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        deleteFlightInfoUseCase = DeleteFlightInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun deleteValidFlightInfo_verifyFlightInfoNotExistInRepository_verifyAirportInfoStillExist() = runTest {
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
        val result = deleteFlightInfoUseCase.execute(DeleteFlightInfoUseCase.Param(flightId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat(fakeTripDetailRepository.getFlightAirportInfo(1L)).isNull()
        assertAirportInfoEqual(
            expected = AirportInfo(
                code = "SGN",
                city = "Ho Chi Minh City",
                airportName = "Tan Son Nhat"
            ),
            target = fakeTripDetailRepository.getAirportInfo("SGN")
        )
        assertAirportInfoEqual(
            expected = AirportInfo(
                code = "SIN",
                city = "Singapore",
                airportName = "Changi"
            ),
            target = fakeTripDetailRepository.getAirportInfo("SIN")
        )
    }

    @Test
    fun deleteValidFlightInfo_dataNotExist_canReturnError() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When
        val result = deleteFlightInfoUseCase.execute(DeleteFlightInfoUseCase.Param(flightId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteFlightInfo::class.java)
    }

    @Test
    fun deleteValidFlightInfo_throwExceptionDeleteFlightInfoFromRepository_verifyInfoStillExist() = runTest {
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

        fakeTripDetailRepository.forceError = true

        // When
        val result = deleteFlightInfoUseCase.execute(DeleteFlightInfoUseCase.Param(flightId = 1L)).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        val error = (result[1] as Result.Error).exception
        Truth.assertThat(error).isInstanceOf(ExceptionDeleteFlightInfo::class.java)
    }
}