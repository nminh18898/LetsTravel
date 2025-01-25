package com.minhhnn18898.manage_trip.trip_detail.data.repo.memories

import android.content.Context
import android.net.Uri
import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.core.utils.imageSize
import com.minhhnn18898.manage_trip.trip_detail.data.dao.memories.TripPhotoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripPhotoInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripPhotoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.toListTripPhotoInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TripPhotoRepositoryImpl(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val photoInfoDao: TripPhotoDao,
    private val context: Context
): TripPhotoRepository {
    override fun getPhotosStream(tripId: Long): Flow<List<TripPhotoInfo>> {
        return photoInfoDao
            .getTripPhotos(tripId)
            .map {
                it.toListTripPhotoInfo()
            }

    }

    override suspend fun insertPhoto(tripId: Long, photoUri: Uri): Long = withContext(ioDispatcher) {
        val (width, height) = photoUri.imageSize(context)

        val photoModel = TripPhotoModel(
            photoId = 0,
            photoUri = photoUri.toString(),
            width = width,
            height = height,
            tripId = tripId
        )

        val resultCode = photoInfoDao.insert(photoModel)

        if(resultCode == -1L) {
            throw ExceptionAddTripPhoto()
        }

        resultCode
    }

    override suspend fun deletePhoto(photoId: Long) = withContext(ioDispatcher) {
        val result = photoInfoDao.delete(photoId)

        if(result <= 0) {
            throw ExceptionDeleteTripPhoto()
        }
    }
}