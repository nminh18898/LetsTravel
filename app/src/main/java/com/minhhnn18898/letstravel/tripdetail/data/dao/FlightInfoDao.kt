package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfo

@Dao
interface FlightInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg flightInfo: FlightInfo): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flightInfo: FlightInfo): Long

    @Delete
    suspend fun delete(flightInfo: FlightInfo)

    @Query("SELECT * FROM flight_info")
    suspend fun getAll(): List<FlightInfo>
}