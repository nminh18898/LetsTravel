package com.minhhnn18898.letstravel.tripdetail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel

@Entity(
    tableName = "hotel_info",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HotelInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("hotel_id")
    val hotelId: Long,
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
    val hotelId: Long = 0,
    val hotelName: String,
    val address: String,
    val checkInDate: Long,
    val checkOutDate: Long,
    val price: Long,
)

fun HotelInfo.toHotelInfoModel(tripId: Long): HotelInfoModel {
    return HotelInfoModel(
        hotelId = this.hotelId,
        tripId = tripId,
        hotelName =  this.hotelName,
        address =  this.address,
        price = this.price,
        checkInDate =  this.checkInDate,
        checkOutDate = this.checkOutDate
    )
}