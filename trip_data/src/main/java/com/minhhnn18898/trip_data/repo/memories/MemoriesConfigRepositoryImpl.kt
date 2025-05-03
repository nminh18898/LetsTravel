package com.minhhnn18898.trip_data.repo.memories

import com.minhhnn18898.trip_data.dao.memories.TripMemoriesConfigDao
import com.minhhnn18898.trip_data.model.memories.TripMemoriesConfigInfo
import com.minhhnn18898.trip_data.model.memories.toTripMemoriesConfigInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MemoriesConfigRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val configInfoDao: TripMemoriesConfigDao,
): MemoriesConfigRepository {

    override fun getConfigStream(tripId: Long): Flow<TripMemoriesConfigInfo> {
        return configInfoDao
            .getConfigStream(tripId)
            .map {
                it?.toTripMemoriesConfigInfo() ?: TripMemoriesConfigInfo(photoFrameType = MemoriesConfigRepository.PHOTO_FRAME_DEFAULT)
            }
    }

    override suspend fun upsertConfig(tripId: Long, config: TripMemoriesConfigInfo): Long = withContext(ioDispatcher) {
        val dataModel = config.toTripMemoriesConfigInfo(tripId)

        val resultCode = configInfoDao.upsert(dataModel)

        if(resultCode <= 0L) {
            throw ExceptionUpdateMemoriesConfig()
        }

        resultCode
    }
}