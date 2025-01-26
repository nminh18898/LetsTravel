package com.minhhnn18898.manage_trip.trip_detail.domain.memories_config

import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripMemoriesConfigInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.MemoriesConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMemoriesConfigUseCase @Inject constructor(private val repository: MemoriesConfigRepository) {

    fun execute(tripId: Long) : Flow<TripMemoriesConfigInfo> = repository.getConfigStream(tripId)
}