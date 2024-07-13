package com.minhhnn18898.letstravel.triplisting.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.triplisting.model.TripInfo

@Dao
interface TripInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg tripInfo: TripInfo): List<Long>

    @Delete
    suspend fun delete(tripInfo: TripInfo)

    @Query("SELECT * FROM trip_info")
    suspend fun getAll(): List<TripInfo>

}