package com.minhhnn18898.trip_data.dao.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.trip_data.model.plan.AirportInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(airportInfoModel: AirportInfoModel): Long

    @Update
    suspend fun update(airportInfoModel: AirportInfoModel): Int

    @Query("DELETE FROM airport_info WHERE airport_code = :airportCode")
    suspend fun delete(airportCode: String): Int

    @Query("SELECT * FROM airport_info WHERE airport_code=:code")
    fun get(code: String): Flow<AirportInfoModel?>
}