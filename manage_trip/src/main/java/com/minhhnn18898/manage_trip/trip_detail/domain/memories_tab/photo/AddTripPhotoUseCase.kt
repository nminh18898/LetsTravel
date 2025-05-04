package com.minhhnn18898.manage_trip.trip_detail.domain.memories_tab.photo

import android.net.Uri
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.trip_data.repo.memories.TripPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddTripPhotoUseCase @Inject constructor(private val repository: TripPhotoRepository) {

    fun execute(tripId: Long, uris: List<Uri>): Flow<Result<Int>> = flow {
        emit(Result.Loading)
        val result = repository.insertPhoto(
            tripId = tripId,
            photoUris = uris
        )
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }
}