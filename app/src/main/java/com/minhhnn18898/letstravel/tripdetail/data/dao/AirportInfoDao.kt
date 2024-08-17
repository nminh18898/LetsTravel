package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfoModel
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
    fun get(code: String): AirportInfoModel

    @Query("SELECT * FROM airport_info")
    fun getAll(): Flow<List<AirportInfoModel>>
}