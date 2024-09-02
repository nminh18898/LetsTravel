package com.minhhnn18898.manage_trip.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.manage_trip.tripdetail.data.model.FlightInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flightInfo: FlightInfoModel): Long

    @Update
    suspend fun update(flightInfo: FlightInfoModel): Int

    @Query("DELETE FROM flight_info WHERE flight_id = :flightId")
    suspend fun delete(flightId: Long): Int

    @Query("SELECT * FROM flight_info WHERE trip_id=:tripId ORDER BY departure_time ASC")
    fun getFlights(tripId: Long): Flow<List<FlightInfoModel>>

    @Query("SELECT * FROM flight_info WHERE flight_id=:flightId")
    fun getFlight(flightId: Long): Flow<FlightInfoModel?>
}