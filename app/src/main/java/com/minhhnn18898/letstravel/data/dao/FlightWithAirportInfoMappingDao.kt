package com.minhhnn18898.letstravel.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.data.model.FlightInfoWithAirport
import com.minhhnn18898.letstravel.data.model.FlightWithAirportInfoMapping

@Dao
interface FlightWithAirportInfoMappingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flightAndAirportMapping: FlightWithAirportInfoMapping): Long

    @Delete
    suspend fun delete(flightAndAirportMapping: FlightWithAirportInfoMapping)

    @Query("SELECT * FROM flight_info WHERE flight_id = :flightId")
    suspend fun get(flightId: Int): List<FlightInfoWithAirport>
}