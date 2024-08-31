package com.minhhnn18898.manage_trip.tripdetail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel

@Entity(
    tableName = "trip_activity_info",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class TripActivityInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("activity_id")
    val activityId: Long,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("photo")
    val photo: String,
    @ColumnInfo("time_from")
    val timeFrom: Long?,
    @ColumnInfo("time_to")
    val timeTo: Long?,
    @ColumnInfo("price")
    val price: Long,

    // Relation mapping
    @ColumnInfo("trip_id")
    val tripId: Long,
)

data class TripActivityInfo(
    val activityId: Long,
    val title: String,
    val description: String,
    val photo: String,
    val timeFrom: Long?,
    val timeTo: Long?,
    val price: Long,
)

fun TripActivityInfoModel.toTripActivityInfo(): TripActivityInfo {
    return TripActivityInfo(
        activityId = this.activityId,
        title = this.title,
        description = this.description,
        photo = this.photo,
        timeFrom = this.timeFrom,
        timeTo = this.timeTo,
        price = this.price
    )
}

fun List<TripActivityInfoModel>.toTripActivityInfo(): List<TripActivityInfo> {
    return this.map { it.toTripActivityInfo() }
}

fun TripActivityInfo.toTripActivityModel(tripId: Long): TripActivityInfoModel {
    return TripActivityInfoModel(
        activityId = this.activityId,
        title = this.title,
        description = this.description,
        photo = this.photo,
        timeFrom = this.timeFrom,
        timeTo = this.timeTo,
        price = this.price,
        tripId =  tripId
    )
}