package com.minhhnn18898.trip_data.dao.memories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.trip_data.model.memories.TripPhotoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TripPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tripPhotoModels: List<TripPhotoModel>): List<Long>

    @Query("DELETE FROM trip_photos WHERE photo_id = :photoId")
    suspend fun delete(photoId: Long): Int

    @Query("SELECT * FROM trip_photos WHERE trip_id=:tripId")
    fun getTripPhotos(tripId: Long): Flow<List<TripPhotoModel>>
}