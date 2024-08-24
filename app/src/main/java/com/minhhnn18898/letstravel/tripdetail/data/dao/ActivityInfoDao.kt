package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.letstravel.tripdetail.data.model.TripActivityInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activityInfo: TripActivityInfoModel): Long

    @Update
    suspend fun update(activityInfo: TripActivityInfoModel): Int

    @Query("DELETE FROM trip_activity_info WHERE activity_id = :activityId")
    suspend fun delete(activityId: Long): Int

    @Query("SELECT * FROM trip_activity_info WHERE trip_id=:tripId")
    fun getTripActivities(tripId: Long): Flow<List<TripActivityInfoModel>>

    @Query("SELECT * FROM trip_activity_info WHERE activity_id=:activityId")
    fun getTripActivity(activityId: Long): TripActivityInfoModel

}