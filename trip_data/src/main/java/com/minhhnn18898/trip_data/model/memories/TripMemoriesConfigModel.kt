package com.minhhnn18898.trip_data.model.memories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel

@Entity(
    tableName = "trip_memories_config",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TripMemoriesConfigModel(
    @PrimaryKey
    @ColumnInfo("trip_id")
    val tripId: Long,

    @ColumnInfo("photo_frame_type")
    val photoFrameType: Int
)

data class TripMemoriesConfigInfo(
    val photoFrameType: Int = 0
)

fun TripMemoriesConfigModel.toTripMemoriesConfigInfo(): TripMemoriesConfigInfo {
    return TripMemoriesConfigInfo(photoFrameType)
}

fun TripMemoriesConfigInfo.toTripMemoriesConfigInfo(tripId: Long): TripMemoriesConfigModel {
    return TripMemoriesConfigModel(
        tripId = tripId,
        photoFrameType = photoFrameType
    )
}