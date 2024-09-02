package com.minhhnn18898.manage_trip.tripdetail.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TripDetailRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TripDetailRepository
    private lateinit var database: UserTripDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserTripDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

       /* repository =
            TripDetailRepository(
                ioDispatcher = Dispatchers.Main,
                airportInfoDao =  database.airportInfoDao(),
                flightInfoDao = database.flightInfoDao(),
                hotelInfoDao = database.hotelInfoDao(),
                activityInfoDao = database.activityInfoDao(),
                dateTimeFormatter =
            )*/
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertFlightInfo() {
    }

    @Test
    fun updateFlightInfo() {
    }

    @Test
    fun deleteFlightInfo() {
    }

    @Test
    fun getListFlightInfo() {
    }

    @Test
    fun getFlightInfo() {
    }

    @Test
    fun insertHotelInfo() {
    }

    @Test
    fun updateHotelInfo() {
    }

    @Test
    fun getAllHotelInfo() {
    }

    @Test
    fun getHotelInfo() {
    }

    @Test
    fun deleteHotelInfo() {
    }

    @Test
    fun insertActivityInfo() {
    }

    @Test
    fun updateActivityInfo() {
    }

    @Test
    fun deleteActivityInfo() {
    }

    @Test
    fun getAllActivityInfo() {
    }

    @Test
    fun getSortedActivityInfo() {
    }

    @Test
    fun getActivityInfo() {
    }
}