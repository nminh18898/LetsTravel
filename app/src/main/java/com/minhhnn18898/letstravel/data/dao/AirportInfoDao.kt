package com.minhhnn18898.letstravel.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.data.model.AirportInfo

@Dao
interface AirportInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg airportInfo: AirportInfo): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(airportInfo: AirportInfo): Long

    @Delete
    suspend fun delete(airportInfo: AirportInfo)

    @Query("SELECT * FROM airport_info")
    suspend fun getAll(): List<AirportInfo>
}