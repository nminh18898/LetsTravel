package com.minhhnn18898.manage_trip.trip_detail.domain.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.test_helper.FakeTripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.utils.assertActivityInfoEqual
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetSortedListTripActivityInfoUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getSortedListTripActivityInfoUseCase: GetSortedListTripActivityInfoUseCase

    private lateinit var fakeTripDetailRepository: FakeTripDetailRepository

    @Before
    fun setup() {
        fakeTripDetailRepository = FakeTripDetailRepository()
        getSortedListTripActivityInfoUseCase = GetSortedListTripActivityInfoUseCase(fakeTripDetailRepository)
    }

    @After
    fun cleanup() {
        fakeTripDetailRepository.reset()
    }

    @Test
    fun getExistedTripInfo_andUpdateNewValue_returnCorrectValue() = runTest {
        // Given - add some valid activity info so that it can be retrieved later
        val firstActivity = TripActivityInfo(
            activityId = 1L,
            title = "Discover the Delta's Charms",
            description = "Mekong Delta Tour from HCM City",
            photo = "https://testing.com/photo",
            timeFrom = 1_000_000,
            timeTo = 1_500_000,
            price = 2_000_000,
        )

        val secondActivity = TripActivityInfo(
            activityId = 2L,
            title = "Saigon River Tour in Ho Chi Minh",
            description = "Hop on board and join us on a luxury cruise on the Saigon River in the city center of Ho Chi Minh City",
            photo = "https://testing.com/photo2",
            timeFrom = 2_500_000,
            timeTo = 2_800_000,
            price = 2_900_000
        )
        val thirdActivity = TripActivityInfo(
            activityId = 3L,
            title = "Cu Chi Tunnels Tour",
            description = "Used by the Viet Cong during the Vietnam War, the Cu Chi Tunnels are a network of underground tunnels stretching more than 124 miles (200 kilometers)",
            photo = "https://testing.com/photo3",
            timeFrom = 1_600_000,
            timeTo = 1_800_000,
            price = 2_200_000
        )

        fakeTripDetailRepository.apply {
            upsertActivityInfo(tripId = 1L, firstActivity)
            upsertActivityInfo(tripId = 1L, secondActivity)
            upsertActivityInfo(tripId = 1L, thirdActivity)
        }

        // When - 1: get current data from repository
        val dataResult = mutableListOf<Map<Long?, List<TripActivityInfo>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getSortedListTripActivityInfoUseCase.execute(GetSortedListTripActivityInfoUseCase.Param(tripId = 1L)).toList(dataResult)
        }

        // Then - 1
        // list activity that map by the start of day
        val firstDateActivities = dataResult[0][1_000_000]
        val secondDateActivities = dataResult[0][2_000_000]

        //first date has two activities, and the second day has one
        Truth.assertThat(firstDateActivities).hasSize(2)
        Truth.assertThat(secondDateActivities).hasSize(1)
        assertActivityInfoEqual(expected = firstActivity, target = firstDateActivities?.get(0))
        assertActivityInfoEqual(expected = thirdActivity, target = firstDateActivities?.get(1))
        assertActivityInfoEqual(expected = secondActivity, target = secondDateActivities?.get(0))

        // When - 2: update new value for the second trip
        val thirdActivityUpdated = TripActivityInfo(
            activityId = 3L,
            title = "Cu Chi Tunnels Tour - new",
            description = "Used by the Viet Cong during the Vietnam War, the Cu Chi Tunnels are a network of underground tunnels stretching more than 124 miles (200 kilometers) - new",
            photo = "https://testing.com/photo3/new",
            timeFrom = 2_100_000,
            timeTo = 2_200_000,
            price = 2_500_000
        )
        fakeTripDetailRepository.upsertActivityInfo(tripId = 1L, thirdActivityUpdated)

        // Then - 2:
        // list activity that map by the start of day
        val firstDateActivitiesUpdated = dataResult[1][1_000_000]
        val secondDateActivitiesUpdated = dataResult[1][2_000_000]

        // first date has one activity, and the second day has two
        Truth.assertThat(firstDateActivitiesUpdated).hasSize(1)
        Truth.assertThat(secondDateActivitiesUpdated).hasSize(2)
        assertActivityInfoEqual(expected = firstActivity, target = firstDateActivitiesUpdated?.get(0))
        assertActivityInfoEqual(expected = thirdActivityUpdated, target = secondDateActivitiesUpdated?.get(0))
        assertActivityInfoEqual(expected = secondActivity, target = secondDateActivitiesUpdated?.get(1))
    }

    @Test
    fun getNonExistedTripInfo_returnEmptyList() = runTest {
        // Given - make sure repository has no data
        fakeTripDetailRepository.reset()

        // When - get current data from repository
        val dataResult = mutableListOf<Map<Long?, List<TripActivityInfo>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getSortedListTripActivityInfoUseCase.execute(GetSortedListTripActivityInfoUseCase.Param(tripId = 1L)).toList(dataResult)
        }

        // Then
        Truth.assertThat(dataResult[0]).isEmpty()
    }
}