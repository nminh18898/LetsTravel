package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg flightInfo: FlightInfoModel): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flightInfo: FlightInfoModel): Long

    @Delete
    suspend fun delete(flightInfo: FlightInfoModel)

    @Query("SELECT * FROM flight_info WHERE trip_id=:tripId")
    fun getFlights(tripId: Long): Flow<List<FlightInfoModel>>
}