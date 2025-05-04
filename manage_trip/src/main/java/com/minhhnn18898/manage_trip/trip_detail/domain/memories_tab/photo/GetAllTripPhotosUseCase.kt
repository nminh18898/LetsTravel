package com.minhhnn18898.manage_trip.trip_detail.domain.memories_tab.photo

import com.minhhnn18898.trip_data.model.memories.TripPhotoInfo
import com.minhhnn18898.trip_data.repo.memories.TripPhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTripPhotosUseCase @Inject constructor(private val repository: TripPhotoRepository) {

    fun execute(tripId: Long) : Flow<List<TripPhotoInfo>> = repository.getPhotosStream(tripId)
}