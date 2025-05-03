package com.minhhnn18898.trip_data.model.memories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel

@Entity(
    tableName = "trip_photos",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TripPhotoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("photo_id")
    val photoId: Long,
    @ColumnInfo("uri")
    val photoUri: String,
    @ColumnInfo("width")
    val width: Int,
    @ColumnInfo("height")
    val height: Int,

    // Relation mapping
    @ColumnInfo("trip_id")
    val tripId: Long
)

data class TripPhotoInfo(
    val photoId: Long,
    val photoUri: String = "",
    val width: Int = 0,
    val height: Int = 0
)

fun TripPhotoModel.toTripPhotoInfo(): TripPhotoInfo {
    return TripPhotoInfo(
        photoId = photoId,
        photoUri = photoUri,
        width = width,
        height = height
    )
}

fun TripPhotoInfo.TripPhotoModel(tripId: Long): TripPhotoModel {
    return TripPhotoModel(
        photoId = photoId,
        photoUri = photoUri,
        width = width,
        height = height,
        tripId = tripId
    )
}

fun List<TripPhotoModel>.toListTripPhotoInfo(): List<TripPhotoInfo> {
    return this.map { it.toTripPhotoInfo() }
}