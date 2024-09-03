package com.minhhnn18898.manage_trip.tripinfo.data.repo

import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import kotlinx.coroutines.flow.Flow

interface TripInfoRepository {
    fun getAllTrips(): Flow<List<TripInfo>>
    fun getTrip(id: Long): Flow<TripInfo?>
    fun getListDefaultCoverElements(): List<DefaultCoverElement>

    suspend fun insertTripInfo(tripInfoModel: TripInfoModel): Long

    suspend fun updateTripInfo(tripInfoModel: TripInfoModel): Long

    suspend fun deleteTripInfo(tripId: Long)
}