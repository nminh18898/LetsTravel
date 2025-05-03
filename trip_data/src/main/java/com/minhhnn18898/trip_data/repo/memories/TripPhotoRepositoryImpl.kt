package com.minhhnn18898.trip_data.repo.memories

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.core.utils.imageSize
import com.minhhnn18898.trip_data.dao.memories.TripPhotoDao
import com.minhhnn18898.trip_data.model.memories.TripPhotoInfo
import com.minhhnn18898.trip_data.model.memories.TripPhotoModel
import com.minhhnn18898.trip_data.model.memories.toListTripPhotoInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TripPhotoRepositoryImpl(
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

    override suspend fun insertPhoto(tripId: Long, photoUris: List<Uri>): Int = withContext(ioDispatcher) {
        photoUris.grantReadPermission()

        val photoModels = photoUris.map { photoUri ->
            val (width, height) = photoUri.imageSize(context)

            TripPhotoModel(
                photoId = 0,
                photoUri = photoUri.toString(),
                width = width,
                height = height,
                tripId = tripId
            )
        }

        val resultCode = photoInfoDao.insertAll(photoModels).size

        if(resultCode <= 0) {
            throw ExceptionAddTripPhoto()
        }

        resultCode
    }

    private fun List<Uri>.grantReadPermission() {
        forEach {
            context
                .contentResolver
                .takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    override suspend fun deletePhoto(photoId: Long) = withContext(ioDispatcher) {
        val result = photoInfoDao.delete(photoId)

        if(result <= 0) {
            throw ExceptionDeleteTripPhoto()
        }
    }
}