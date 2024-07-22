package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg airportInfo: AirportInfo): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(airportInfo: AirportInfo): Long

    @Delete
    suspend fun delete(airportInfo: AirportInfo)

    @Query("SELECT * FROM airport_info WHERE airport_code=:code")
    fun get(code: String): Flow<AirportInfo>

    @Query("SELECT * FROM airport_info WHERE airport_code IN (:codes)")
    fun getList(codes: List<String>): Flow<AirportInfo>

    @Query("SELECT * FROM airport_info")
    fun getAll(): Flow<List<AirportInfo>>
}