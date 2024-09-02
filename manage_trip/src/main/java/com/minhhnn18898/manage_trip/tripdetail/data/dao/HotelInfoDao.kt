package com.minhhnn18898.manage_trip.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.manage_trip.tripdetail.data.model.HotelInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hotelInfo: HotelInfoModel): Long

    @Update
    suspend fun update(hotelInfo: HotelInfoModel): Int

    @Query("DELETE FROM hotel_info WHERE hotel_id = :hotelId")
    suspend fun delete(hotelId: Long): Int

    @Query("SELECT * FROM hotel_info WHERE trip_id=:tripId ORDER BY check_in_date ASC")
    fun getHotels(tripId: Long): Flow<List<HotelInfoModel>>

    @Query("SELECT * FROM hotel_info WHERE hotel_id=:hotelId")
    fun getHotel(hotelId: Long): Flow<HotelInfoModel?>
}