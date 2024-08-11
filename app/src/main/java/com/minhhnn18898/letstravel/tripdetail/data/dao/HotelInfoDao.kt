package com.minhhnn18898.letstravel.tripdetail.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hotelInfo: HotelInfoModel): Long

    @Delete
    suspend fun delete(hotelInfo: HotelInfoModel)

    @Query("SELECT * FROM hotel_info WHERE trip_id=:tripId")
    fun getHotels(tripId: Long): Flow<List<HotelInfoModel>>
}