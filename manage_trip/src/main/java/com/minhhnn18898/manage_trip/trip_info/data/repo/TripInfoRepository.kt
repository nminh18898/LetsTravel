package com.minhhnn18898.manage_trip.trip_info.data.repo

import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel
import kotlinx.coroutines.flow.Flow

interface TripInfoRepository {
    fun getAllTrips(): Flow<List<TripInfo>>
    fun getTrip(id: Long): Flow<TripInfo?>
    fun getListDefaultCoverElements(): List<DefaultCoverElement>

    suspend fun insertTripInfo(tripInfoModel: TripInfoModel): Long

    suspend fun updateTripInfo(tripInfoModel: TripInfoModel): Long

    suspend fun deleteTripInfo(tripId: Long)
}