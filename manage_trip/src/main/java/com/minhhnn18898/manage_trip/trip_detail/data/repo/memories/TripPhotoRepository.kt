package com.minhhnn18898.manage_trip.trip_detail.data.repo.memories

import android.net.Uri
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripPhotoInfo
import kotlinx.coroutines.flow.Flow

interface TripPhotoRepository {

    fun getPhotosStream(tripId: Long): Flow<List<TripPhotoInfo>>

    suspend fun insertPhoto(tripId: Long, photoUri: Uri): Long

    suspend fun deletePhoto(photoId: Long)
}