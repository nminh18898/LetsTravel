package com.minhhnn18898.letstravel.tripdetail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotel_info")
data class HotelInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("hotel_id")
    val hotelId: Int,
    @ColumnInfo("hotel_name")
    val hotelName: String,
    @ColumnInfo("address")
    val address: String,
    @ColumnInfo("check_in_date")
    val checkInDate: Long,
    @ColumnInfo("check_out_date")
    val checkOutDate: Long,
    @ColumnInfo("price")
    val price: Long,

    // Relation mapping
    @ColumnInfo("trip_id")
    val tripId: Long,
)

data class HotelInfo(
    val hotelId: Int,
    val hotelName: String,
    val address: String,
    val checkInDate: Long,
    val checkOutDate: Long,
    val price: Long,
)