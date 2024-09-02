package com.minhhnn18898.manage_trip.tripdetail.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.tripdetail.data.model.HotelInfoModel
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
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
class HotelInfoDaoTest {

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
    fun insertHotelAndGetById() = runTest {
        // Given
        val hotelInfoModel = HotelInfoModel(
            hotelId = 0,
            tripId = 1,
            hotelName =  "Liberty Central Riverside Hotel",
            address =  "District 1, Ho Chi Minh City",
            price = 2_000_000,
            checkInDate =  100000000,
            checkOutDate = 120000000
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.hotelInfoDao().insert(hotelInfoModel)

        // When
        val loaded = database.hotelInfoDao().getHotel(1L).first()

        // Then
        assertHotelInfoEqual(hotelInfoModel, loaded)
    }

    @Test
    fun updateHotelAndGetById() = runTest {
        // Given
        val hotelInfoModel = HotelInfoModel(
            hotelId = 0,
            tripId = 1,
            hotelName =  "Liberty Central Riverside Hotel",
            address =  "District 1, Ho Chi Minh City",
            price = 2_000_000,
            checkInDate =  100000000,
            checkOutDate = 120000000
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.hotelInfoDao().insert(hotelInfoModel)

        // When
        val updatedValue = HotelInfoModel(
            hotelId = 1,
            tripId = 1,
            hotelName =  "Liberty Central Riverside Hotel - new",
            address =  "District 1, Ho Chi Minh City - new",
            price = 2_500_000,
            checkInDate =  150000000,
            checkOutDate = 200000000
        )
        database.hotelInfoDao().update(updatedValue)
        val loaded = database.hotelInfoDao().getHotel(1L).first()

        // Then
        assertHotelInfoEqual(updatedValue, loaded)
    }

    @Test
    fun deleteHotelAndGetById() = runTest {
        // Given
        val hotelInfoModel = HotelInfoModel(
            hotelId = 0,
            tripId = 1,
            hotelName =  "Liberty Central Riverside Hotel",
            address =  "District 1, Ho Chi Minh City",
            price = 2_000_000,
            checkInDate =  100000000,
            checkOutDate = 120000000
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.hotelInfoDao().insert(hotelInfoModel)

        // When
        database.hotelInfoDao().delete(1L)
        val loaded = database.hotelInfoDao().getHotel(1L).first()

        // Then
        Truth.assertThat(loaded).isNull()
    }

    @Test
    fun getHotelsByTripId_sortedByCheckInTime() = runTest {
        // Given
        val firstHotelInfoModel = HotelInfoModel(
            hotelId = 0,
            tripId = 1,
            hotelName =  "Liberty Central Riverside Hotel",
            address =  "District 1, Ho Chi Minh City",
            price = 2_000_000,
            checkInDate =  150_000_000,
            checkOutDate = 170_000_000
        )
        val secondHotelInfoModel = HotelInfoModel(
            hotelId = 0,
            tripId = 1,
            hotelName =  "Eastin Grand Hotel",
            address =  "Phu Nhuan District, Ho Chi Minh City",
            price = 3_000_000,
            checkInDate =  100_000_000,
            checkOutDate = 120_000_000
        )
        val thirdHotelInfoModel = HotelInfoModel(
            hotelId = 0,
            tripId = 1,
            hotelName =  "Holiday Inn & Suites Saigon Airport",
            address =  "18E Cong Hoa, Ho Chi Minh City",
            price = 2_700_000,
            checkInDate =  180_000_000,
            checkOutDate = 200_000_000
        )

        // insert trip info for foreign key constraint
        insertTripInfo()

        // insert hotel with auto generated id
        database.hotelInfoDao().insert(firstHotelInfoModel)
        database.hotelInfoDao().insert(secondHotelInfoModel)
        database.hotelInfoDao().insert(thirdHotelInfoModel)

        // When
        val loaded = database.hotelInfoDao().getHotels(1L).first()

        // Then - verify that hotel with the earlier check-in date is placed before
        Truth.assertThat(loaded).hasSize(3)
        assertHotelInfoEqual(secondHotelInfoModel, loaded[0])
        assertHotelInfoEqual(firstHotelInfoModel, loaded[1])
        assertHotelInfoEqual(thirdHotelInfoModel, loaded[2])
    }

    private fun assertHotelInfoEqual(expected: HotelInfoModel, target: HotelInfoModel?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.tripId).isEqualTo(expected.tripId)
        Truth.assertThat(target?.hotelName).isEqualTo(expected.hotelName)
        Truth.assertThat(target?.address).isEqualTo(expected.address)
        Truth.assertThat(target?.price).isEqualTo(expected.price)
        Truth.assertThat(target?.checkInDate).isEqualTo(expected.checkInDate)
        Truth.assertThat(target?.checkOutDate).isEqualTo(expected.checkOutDate)
    }
}