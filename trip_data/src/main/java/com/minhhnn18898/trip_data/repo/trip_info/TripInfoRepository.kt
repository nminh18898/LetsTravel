package com.minhhnn18898.trip_data.repo.trip_info

import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import kotlinx.coroutines.flow.Flow

interface TripInfoRepository {
    fun getAllTrips(): Flow<List<TripInfo>>
    fun getTrip(id: Long): Flow<TripInfo?>
    fun getListDefaultCoverElements(): List<DefaultCoverElement>

    suspend fun insertTripInfo(tripInfo: TripInfo): Long

    suspend fun updateTripInfo(tripInfo: TripInfo): Long

    suspend fun deleteTripInfo(tripId: Long)
}