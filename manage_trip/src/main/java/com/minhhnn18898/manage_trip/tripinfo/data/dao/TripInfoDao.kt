package com.minhhnn18898.manage_trip.tripinfo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TripInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tripInfoModel: TripInfoModel): Long

    @Update
    suspend fun update(tripInfoModel: TripInfoModel): Int

    @Query("DELETE FROM trip_info WHERE trip_id = :tripId")
    suspend fun delete(tripId: Long): Int

    @Query("SELECT * FROM trip_info ORDER BY trip_id DESC")
    fun getAll(): Flow<List<TripInfoModel>>

    @Query("SELECT * FROM trip_info WHERE trip_id=:id")
    fun getTripInfo(id: Long): Flow<TripInfoModel?>
}