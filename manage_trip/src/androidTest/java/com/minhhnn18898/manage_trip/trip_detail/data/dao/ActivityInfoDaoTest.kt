package com.minhhnn18898.manage_trip.trip_detail.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.database.UserTripDatabase
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.TripActivityInfoModel
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
class ActivityInfoDaoTest {

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
    fun insertActivityAndGetById() = runTest {
        // Given
        val activityInfoModel = TripActivityInfoModel(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
            tripId =  1L
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.activityInfoDao().insert(activityInfoModel)

        // When
        val loaded = database.activityInfoDao().getTripActivity(1L).first()

        // Then
        assertActivityInfoEqual(activityInfoModel, loaded)
    }

    @Test
    fun updateActivityAndGetById() = runTest {
        // Given
        val activityInfoModel = TripActivityInfoModel(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
            tripId =  1L
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.activityInfoDao().insert(activityInfoModel)

        // When
        val updatedValue = TripActivityInfoModel(
            activityId = 1L,
            title = "Discover the Delta's Charms - new",
            description = "Mekong Delta Tour from HCM City - new",
            photo = "https://testing.com/photo/new",
            timeFrom = 1_200_000,
            timeTo = 1_400_000,
            price = 2_200_000,
            tripId =  1L
        )
        database.activityInfoDao().update(updatedValue)
        val loaded = database.activityInfoDao().getTripActivity(1L).first()

        // Then
        assertActivityInfoEqual(updatedValue, loaded)
    }

    @Test
    fun deleteActivityAndGetById() = runTest {
        // Given
        val activityInfoModel = TripActivityInfoModel(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
            tripId =  1L
        )
        // insert trip info for foreign key constraint
        insertTripInfo()
        // insert with auto generated id
        database.activityInfoDao().insert(activityInfoModel)

        // When
        database.activityInfoDao().delete(1L)
        val loaded = database.activityInfoDao().getTripActivity(1L).first()

        // Then
        Truth.assertThat(loaded).isNull()
    }

    @Test
    fun getActivitiesByTripId_sortedByStartTime() = runTest {
        // Given
        val firstActivity = TripActivityInfoModel(
            activityId = 0L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo1",
            timeFrom = 1_500_000,
            timeTo = 1_800_000,
            price = 2_000_000,
            tripId =  1L
        )
        val secondActivity = TripActivityInfoModel(
            activityId = 0L,
            title = "Saigon River Tour in Ho Chi Minh",
            description = "Hop on board and join us on a luxury cruise on the Saigon River in the city center of Ho Chi Minh City",
            photo = "https://testing.com/photo2",
            timeFrom = 1_000_000,
            timeTo = 1_200_000,
            price = 2_900_000,
            tripId =  1L
        )
        val thirdActivity = TripActivityInfoModel(
            activityId = 0L,
            title = "Cu Chi Tunnels Tour",
            description = "Used by the Viet Cong during the Vietnam War, the Cu Chi Tunnels are a network of underground tunnels stretching more than 124 miles (200 kilometers)",
            photo = "https://testing.com/photo3",
            timeFrom = 1_700_000,
            timeTo = 2_000_000,
            price = 2_200_000,
            tripId =  1L
        )
        // insert trip info for foreign key constraint
        insertTripInfo()

        // insert with auto generated id
        database.activityInfoDao().insert(firstActivity)
        database.activityInfoDao().insert(secondActivity)
        database.activityInfoDao().insert(thirdActivity)

        // When
        val loaded = database.activityInfoDao().getTripActivities(1L).first()

        // Then
        Truth.assertThat(loaded).hasSize(3)
        assertActivityInfoEqual(secondActivity, loaded[0])
        assertActivityInfoEqual(firstActivity, loaded[1])
        assertActivityInfoEqual(thirdActivity, loaded[2])
    }

    private fun assertActivityInfoEqual(expected: TripActivityInfoModel, target: TripActivityInfoModel?) {
        Truth.assertThat(target).isNotNull()
        Truth.assertThat(target?.title).isEqualTo(expected.title)
        Truth.assertThat(target?.description).isEqualTo(expected.description)
        Truth.assertThat(target?.photo).isEqualTo(expected.photo)
        Truth.assertThat(target?.timeFrom).isEqualTo(expected.timeFrom)
        Truth.assertThat(target?.timeTo).isEqualTo(expected.timeTo)
        Truth.assertThat(target?.price).isEqualTo(expected.price)
        Truth.assertThat(target?.tripId).isEqualTo(expected.tripId)
    }
}