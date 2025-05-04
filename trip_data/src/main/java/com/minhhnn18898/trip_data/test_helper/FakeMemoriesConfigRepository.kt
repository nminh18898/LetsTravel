package com.minhhnn18898.trip_data.test_helper

import com.minhhnn18898.trip_data.model.memories.TripMemoriesConfigInfo
import com.minhhnn18898.trip_data.repo.memories.MemoriesConfigRepository
import kotlinx.coroutines.flow.Flow

class FakeMemoriesConfigRepository: MemoriesConfigRepository {
    override fun getConfigStream(tripId: Long): Flow<TripMemoriesConfigInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertConfig(tripId: Long, config: TripMemoriesConfigInfo): Long {
        TODO("Not yet implemented")
    }
}