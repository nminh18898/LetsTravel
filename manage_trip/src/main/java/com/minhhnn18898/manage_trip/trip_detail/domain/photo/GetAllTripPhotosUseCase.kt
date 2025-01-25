package com.minhhnn18898.manage_trip.trip_detail.domain.photo

import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripPhotoInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.TripPhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTripPhotosUseCase @Inject constructor(private val repository: TripPhotoRepository) {

    fun execute(tripId: Long) : Flow<List<TripPhotoInfo>> = repository.getPhotosStream(tripId)
}