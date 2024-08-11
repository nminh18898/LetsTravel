package com.minhhnn18898.letstravel.tripinfo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TripInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tripInfoModel: TripInfoModel): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tripInfoModel: TripInfoModel): Long

    @Delete
    suspend fun delete(tripInfoModel: TripInfoModel)

    @Query("SELECT * FROM trip_info ORDER BY trip_id DESC")
    fun getAll(): Flow<List<TripInfoModel>>

    @Query("SELECT * FROM trip_info WHERE trip_id=:id")
    fun getTripInfo(id: Long): Flow<TripInfoModel>
}