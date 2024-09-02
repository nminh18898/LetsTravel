package com.minhhnn18898.manage_trip.tripdetail.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.tripdetail.data.model.AirportInfoModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("SpellCheckingInspection")
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AirportInfoDaoTest {

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

    @Test
    fun insertAirportAndGetByCode() = runTest {
        // Given
        val airportInfoModel = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        database.airportInfoDao().insert(airportInfoModel)

        // When
        val loaded = database.airportInfoDao().get("SGN").first()

        // Then
        assertAirportInfoEqual(airportInfoModel, loaded)
    }

    @Test
    fun insertMultipleAirport_retrieveLatestValue() = runTest {
        // Given - When
        val firstInfo = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        val secondInfo = AirportInfoModel(
            code = "SGN",
            city = "HCmC",
            airportName = "Tan Son Nhat International Airport"
        )

        database.airportInfoDao().insert(firstInfo)
        database.airportInfoDao().insert(secondInfo)
        val loaded = database.airportInfoDao().get("SGN").first()

        // Then
        assertAirportInfoEqual(secondInfo, loaded)
    }

    @Test
    fun updateAirportAndGetByCode() = runTest {
        // Given
        val firstInfo = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )
        database.airportInfoDao().insert(firstInfo)

        // When
        val updatedInfo = AirportInfoModel(
            code = "SGN",
            city = "HCMC",
            airportName = "Tan Son Nhat International Airport"
        )
        database.airportInfoDao().insert(updatedInfo)
        val loaded = database.airportInfoDao().get("SGN").first()

        // Then
        assertAirportInfoEqual(updatedInfo, loaded)
    }

    @Test
    fun deleteAirportAndGetByCode() = runTest {
        // Given
        val airportInfoModel = AirportInfoModel(
            code = "SGN",
            city = "Ho Chi Minh City",
            airportName = "Tan Son Nhat"
        )

        database.airportInfoDao().insert(airportInfoModel)

        // When
        database.airportInfoDao().delete("SGN")
        val loaded = database.airportInfoDao().get("SGN").first()

        // Then
        Truth.assertThat(loaded).isNull()
    }

    private fun assertAirportInfoEqual(expected: AirportInfoModel, target: AirportInfoModel?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.code).isEqualTo(expected.code)
        Truth.assertThat(target?.city).isEqualTo(expected.city)
        Truth.assertThat(target?.airportName).isEqualTo(expected.airportName)
    }
}