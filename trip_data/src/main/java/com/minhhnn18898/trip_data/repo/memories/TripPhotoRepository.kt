package com.minhhnn18898.trip_data.repo.memories

import android.net.Uri
import com.minhhnn18898.trip_data.model.memories.TripPhotoInfo
import kotlinx.coroutines.flow.Flow

interface TripPhotoRepository {

    fun getPhotosStream(tripId: Long): Flow<List<TripPhotoInfo>>

    suspend fun insertPhoto(tripId: Long, photoUris: List<Uri>): Int

    suspend fun deletePhoto(photoId: Long)
}