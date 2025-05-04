package com.minhhnn18898.trip_data.test_helper

import android.net.Uri
import com.minhhnn18898.trip_data.model.memories.TripPhotoInfo
import com.minhhnn18898.trip_data.repo.memories.TripPhotoRepository
import kotlinx.coroutines.flow.Flow

class FakeTripPhotoRepository: TripPhotoRepository {
    override fun getPhotosStream(tripId: Long): Flow<List<TripPhotoInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPhoto(tripId: Long, photoUris: List<Uri>): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deletePhoto(photoId: Long) {
        TODO("Not yet implemented")
    }

}