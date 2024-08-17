package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hotelInfo: HotelInfoModel): Long

    @Update
    suspend fun update(hotelInfo: HotelInfoModel): Int

    @Delete
    suspend fun delete(hotelInfo: HotelInfoModel)

    @Query("SELECT * FROM hotel_info WHERE trip_id=:tripId")
    fun getHotels(tripId: Long): Flow<List<HotelInfoModel>>

    @Query("SELECT * FROM hotel_info WHERE hotel_id=:hotelId")
    fun getHotel(hotelId: Long): HotelInfoModel
}