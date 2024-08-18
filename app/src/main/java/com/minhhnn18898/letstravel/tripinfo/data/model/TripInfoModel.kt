package com.minhhnn18898.letstravel.tripinfo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_info")
data class TripInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("trip_id")
    val tripId: Long,
    val title: String,
    @ColumnInfo("cover_type")
    val coverType: Int,
    @ColumnInfo("default_cover_id")
    val defaultCoverId: Int,
    @ColumnInfo("custom_cover_path")
    val customCoverPath: String
) {
    companion object {
        const val TRIP_COVER_TYPE_DEFAULT = 1
        const val TRIP_COVER_TYPE_CUSTOM = 2
    }
}

data class TripInfo(
    val tripId: Long,
    val title: String,
    val coverType: Int,
    val defaultCoverId: Int = 0,
    val customCoverPath: String = ""
)

fun TripInfoModel.toTripInfo(): TripInfo {
    return TripInfo(
        tripId = tripId,
        title = title,
        coverType = coverType,
        defaultCoverId = defaultCoverId,
        customCoverPath = customCoverPath
    )
}