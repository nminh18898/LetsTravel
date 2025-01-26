package com.minhhnn18898.manage_trip.trip_detail.data.dao.memories

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripMemoriesConfigModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TripMemoriesConfigDao {
    @Upsert
    suspend fun upsert(configModel: TripMemoriesConfigModel): Long

    @Query("SELECT * FROM trip_memories_config WHERE trip_id=:tripId")
    fun getConfigStream(tripId: Long): Flow<TripMemoriesConfigModel?>

    @Query("SELECT * FROM trip_memories_config WHERE trip_id=:tripId")
    fun getConfig(tripId: Long): TripMemoriesConfigModel?
}