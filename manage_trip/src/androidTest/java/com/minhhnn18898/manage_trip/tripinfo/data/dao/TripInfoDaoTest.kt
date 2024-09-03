package com.minhhnn18898.manage_trip.tripinfo.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.tripinfo.utils.assertTripInfoModelEqual
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
class TripInfoDaoTest {
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
    fun insertTripAndGetById() = runTest {
        // Given
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        // insert with auto generated id
        database.tripInfoDao().insert(tripInfo)

        // When
        val loaded = database.tripInfoDao().getTripInfo(1L).first()

        // Then
        assertTripInfoModelEqual(tripInfo, loaded)
    }

    @Test
    fun updateTripAndGetById() = runTest {
        // Given
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        // insert with auto generated id
        database.tripInfoDao().insert(tripInfo)

        // When
        val updatedValue = tripInfo.copy(
            tripId = 1,
            title = "Ho Chi Minh City - Vietnam",
            coverType = 1,
            defaultCoverId = 2,
            customCoverPath = "https://testing.com/new"
        )
        database.tripInfoDao().update(updatedValue)
        val loaded = database.tripInfoDao().getTripInfo(1L).first()

        // Then
        assertTripInfoModelEqual(updatedValue, loaded)
    }

    @Test
    fun deleteTripAndGetById() = runTest {
        // Given
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        // insert with auto generated id
        database.tripInfoDao().insert(tripInfo)

        // When
        database.tripInfoDao().delete(1)
        val loaded = database.tripInfoDao().getTripInfo(1L).first()

        // Then
        Truth.assertThat(loaded).isNull()
    }

    @Test
    fun getAllTrip() = runTest {
        // Given
        val firstTrip = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com/vietnam"
        )
        val secondTrip = TripInfoModel(
            tripId = 0,
            title = "Thailand",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com/thailand"
        )
        // insert with auto generated id
        database.tripInfoDao().insert(firstTrip)
        database.tripInfoDao().insert(secondTrip)

        // When
        val loaded = database.tripInfoDao().getAll().first()

        // Then - verify sorting order that the newest value is placed first
        Truth.assertThat(loaded).hasSize(2)
        assertTripInfoModelEqual(loaded[0], secondTrip)
        assertTripInfoModelEqual(loaded[1], firstTrip)
    }
}