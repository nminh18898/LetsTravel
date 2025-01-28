package com.minhhnn18898.manage_trip.trip_detail.domain.memories_tab.photo

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.TripPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTripPhotoUseCase @Inject constructor(private val repository: TripPhotoRepository) {

    fun execute(photoId: Long): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deletePhoto(photoId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}