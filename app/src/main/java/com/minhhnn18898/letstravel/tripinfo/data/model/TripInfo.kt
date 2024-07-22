package com.minhhnn18898.letstravel.tripinfo.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfoModel

@Entity(tableName = "trip_info")
data class TripInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("trip_id")
    val tripId: Long,
    val title: String,
    @ColumnInfo("default_cover_id")
    val defaultCoverId: Int,
)

data class TripAndFlightInfo(
    @Embedded val tripInfo: TripInfo,
    @Relation(
        parentColumn = "trip_id",
        entityColumn = "trip_id"
    )
    val flightInfo: FlightInfoModel
)