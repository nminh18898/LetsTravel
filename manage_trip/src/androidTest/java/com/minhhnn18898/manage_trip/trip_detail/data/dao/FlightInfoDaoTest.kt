package com.minhhnn18898.manage_trip.trip_detail.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.FlightInfoModel
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FlightInfoDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: UserTripDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            UserTripDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun cleanup() = database.close()

    private fun insertTripInfo() = runTest {
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )

        // insert with auto generated id
        database.tripInfoDao().insert(tripInfo)
    }

    @Test
    fun insertFlightAndGetById() = runTest {
        // Given
        val flightInfoModel = FlightInfoModel(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_500_000,
            price = 2_000_000,
            tripId = 1L,
            departAirportCode = "SGN",
            destinationAirportCode = "HAN"
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.flightInfoDao().insert(flightInfoModel)

        // When
        val loaded = database.flightInfoDao().getFlight(1L).first()

        // Then
        assertFlightInfoEqual(flightInfoModel, loaded)
    }

    @Test
    fun updateFlightAndGetById() = runTest {
        // Given
        val flightInfoModel = FlightInfoModel(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_500_000,
            price = 2_000_000,
            tripId = 1L,
            departAirportCode = "SGN",
            destinationAirportCode = "HAN"
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.flightInfoDao().insert(flightInfoModel)

        // When
        val updatedValue = FlightInfoModel(
            flightId = 1L,
            flightNumber = "VN 363 - new",
            operatedAirlines = "Vietnam Airlines - new",
            departureTime = 1_200_000,
            arrivalTime = 1_800_000,
            price = 2_200_000,
            tripId = 1L,
            departAirportCode = "SGN - new",
            destinationAirportCode = "HAN - new"
        )
        database.flightInfoDao().update(updatedValue)
        val loaded = database.flightInfoDao().getFlight(1L).first()

        // Then
        assertFlightInfoEqual(updatedValue, loaded)
    }

    @Test
    fun deleteFlightAndGetById() = runTest {
        // Given
        val flightInfoModel = FlightInfoModel(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_000_000,
            arrivalTime = 1_500_000,
            price = 2_000_000,
            tripId = 1L,
            departAirportCode = "SGN",
            destinationAirportCode = "HAN"
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.flightInfoDao().insert(flightInfoModel)

        // When
        database.flightInfoDao().delete(1L)
        val loaded = database.flightInfoDao().getFlight(1L).first()

        // Then
        Truth.assertThat(loaded).isNull()
    }

    @Test
    fun getMultipleFlightByTripId_sortedByDepartTime() = runTest {
        // Given
        val firstFlight = FlightInfoModel(
            flightId = 0L,
            flightNumber = "VN 363",
            operatedAirlines = "Vietnam Airlines",
            departureTime = 1_600_000,
            arrivalTime = 1_800_000,
            price = 2_000_000,
            tripId = 1L,
            departAirportCode = "SGN",
            destinationAirportCode = "HAN"
        )
        val secondFlight = FlightInfoModel(
            flightId = 0L,
            flightNumber = "TW 131",
            operatedAirlines = "Thai Airways",
            departureTime = 1_200_000,
            arrivalTime = 1_400_000,
            price = 2_200_000,
            tripId = 1L,
            departAirportCode = "HAN",
            destinationAirportCode = "BKK"
        )
        val thirdFlight = FlightInfoModel(
            flightId = 0L,
            flightNumber = "SG 378",
            operatedAirlines = "Singapore Airlines",
            departureTime = 2_000_000,
            arrivalTime = 2_500_000,
            price = 3_000_000,
            tripId = 1L,
            departAirportCode = "BKK",
            destinationAirportCode = "SIN"
        )
        // insert trip info for foreign key constraint
        insertTripInfo()

        // insert flight with auto generated id
        database.flightInfoDao().insert(firstFlight)
        database.flightInfoDao().insert(secondFlight)
        database.flightInfoDao().insert(thirdFlight)

        // When
        val loaded = database.flightInfoDao().getFlights(1L).first()

        // Then
        Truth.assertThat(loaded).hasSize(3)
        assertFlightInfoEqual(secondFlight, loaded[0])
        assertFlightInfoEqual(firstFlight, loaded[1])
        assertFlightInfoEqual(thirdFlight, loaded[2])
    }

    private fun assertFlightInfoEqual(expected: FlightInfoModel, target: FlightInfoModel?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.flightNumber).isEqualTo(expected.flightNumber)
        Truth.assertThat(target?.operatedAirlines).isEqualTo(expected.operatedAirlines)
        Truth.assertThat(target?.departureTime).isEqualTo(expected.departureTime)
        Truth.assertThat(target?.arrivalTime).isEqualTo(expected.arrivalTime)
        Truth.assertThat(target?.price).isEqualTo(expected.price)
        Truth.assertThat(target?.tripId).isEqualTo(expected.tripId)
        Truth.assertThat(target?.departAirportCode).isEqualTo(expected.departAirportCode)
        Truth.assertThat(target?.destinationAirportCode).isEqualTo(expected.destinationAirportCode)
    }
}