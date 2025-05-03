package com.minhhnn18898.trip_data.repo.memories

import com.minhhnn18898.trip_data.model.memories.TripMemoriesConfigInfo
import kotlinx.coroutines.flow.Flow

interface MemoriesConfigRepository {

    companion object {
        const val PHOTO_FRAME_DEFAULT = 0
        const val PHOTO_FRAME_VINTAGE = 1
        const val PHOTO_FRAME_FLOWER = 2
        const val PHOTO_FRAME_COLORFUL = 3
    }

    fun getConfigStream(tripId: Long): Flow<TripMemoriesConfigInfo>

    suspend fun upsertConfig(tripId: Long, config: TripMemoriesConfigInfo): Long
}