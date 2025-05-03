package com.minhhnn18898.trip_data.dao.memories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.trip_data.model.memories.TripMemoriesConfigModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TripMemoriesConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(configModel: TripMemoriesConfigModel): Long

    @Query("SELECT * FROM trip_memories_config WHERE trip_id=:tripId")
    fun getConfigStream(tripId: Long): Flow<TripMemoriesConfigModel?>

    @Query("SELECT * FROM trip_memories_config WHERE trip_id=:tripId")
    fun getConfig(tripId: Long): TripMemoriesConfigModel?
}