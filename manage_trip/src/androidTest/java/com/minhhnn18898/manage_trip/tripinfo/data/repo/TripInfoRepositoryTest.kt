package com.minhhnn18898.manage_trip.tripinfo.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
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
@MediumTest
class TripInfoRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var repository: TripInfoRepository
    private lateinit var database: UserTripDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserTripDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository =
            TripInfoRepository(
                database.tripInfoDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun saveTrip_retrieveTrip() = runTest {
        // Given
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        repository.insertTripInfo(tripInfo)

        // When
        val result = repository.getTrip(1L).first()

        // Then
        val expectedValue = TripInfo(
            tripId = 1,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        assertTripInfoEqual(expectedValue, result)
    }

    @Test
    fun updateTrip_retrieveTrip() = runTest {
        // Given
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        repository.insertTripInfo(tripInfo)

        // When
        val updatedValue = tripInfo.copy(
            tripId = 1,
            title = "Ho Chi Minh City - Vietnam",
            coverType = 1,
            defaultCoverId = 2,
            customCoverPath = "https://testing.com/new"
        )
        repository.updateTripInfo(updatedValue)
        val result = repository.getTrip(1L).first()

        // Then
        val expectedValue = TripInfo(
            tripId = 1,
            title = "Ho Chi Minh City - Vietnam",
            coverType = 1,
            defaultCoverId = 2,
            customCoverPath = "https://testing.com/new"
        )
        assertTripInfoEqual(expectedValue, result)
    }

    @Test
    fun deleteTrip_retrieveTrip() = runTest {
        // Given
        val tripInfo = TripInfoModel(
            tripId = 0,
            title = "Vietnam",
            coverType = 1,
            defaultCoverId = 1,
            customCoverPath = "https://testing.com"
        )
        repository.insertTripInfo(tripInfo)

        // When
        repository.deleteTripInfo(1L)
        val result = repository.getTrip(1L).first()

        // Then
        Truth.assertThat(result).isNull()
    }

    @Test
    fun getAllTrips() = runTest {
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
        repository.insertTripInfo(firstTrip)
        repository.insertTripInfo(secondTrip)

        // When
        val result = repository.getAllTrips().first()

        // Then
        Truth.assertThat(result).hasSize(2)
        assertTripInfoEqual(
            result[0],
            TripInfo(
                tripId = 2,
                title = "Thailand",
                coverType = 1,
                defaultCoverId = 1,
                customCoverPath = "https://testing.com/thailand"
            )
        )
        assertTripInfoEqual(
            result[1],
            TripInfo(
                tripId = 1,
                title = "Vietnam",
                coverType = 1,
                defaultCoverId = 1,
                customCoverPath = "https://testing.com/vietnam"
            )
        )
    }

    @Test
    fun getListDefaultCover() {
        // When
        val result = repository.getListDefaultCoverElements()

        // Then
        Truth.assertThat(result).isEqualTo(
            mutableListOf(
                DefaultCoverElement.COVER_DEFAULT_THEME_SPRING,
                DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER,
                DefaultCoverElement.COVER_DEFAULT_THEME_AUTUMN,
                DefaultCoverElement.COVER_DEFAULT_THEME_WINTER,
                DefaultCoverElement.COVER_DEFAULT_THEME_BEACH,
                DefaultCoverElement.COVER_DEFAULT_THEME_MOUNTAIN,
                DefaultCoverElement.COVER_DEFAULT_THEME_AURORA,
                DefaultCoverElement.COVER_DEFAULT_THEME_VIETNAM,
                DefaultCoverElement.COVER_DEFAULT_THEME_CHINA,
                DefaultCoverElement.COVER_DEFAULT_THEME_SEA_DIVING,
            )
        )
    }

    private fun assertTripInfoEqual(expected: TripInfo, target: TripInfo?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.title).isEqualTo(expected.title)
        Truth.assertThat(target?.coverType).isEqualTo(expected.coverType)
        Truth.assertThat(target?.defaultCoverId).isEqualTo(expected.defaultCoverId)
        Truth.assertThat(target?.customCoverPath).isEqualTo(expected.customCoverPath)
    }
}