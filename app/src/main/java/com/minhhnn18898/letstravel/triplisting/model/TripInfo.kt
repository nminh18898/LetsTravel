package com.minhhnn18898.letstravel.triplisting.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.minhhnn18898.letstravel.data.model.FlightInfo

@Entity(tableName = "trip_info")
data class TripInfo(
    @PrimaryKey
    @ColumnInfo("trip_id")
    val tripId: Int,
    val title: String,
    @ColumnInfo("cover_url")
    val coverUrl: String = "",
)

data class TripAndFlightInfo(
    @Embedded val tripInfo: TripInfo,
    @Relation(
        parentColumn = "trip_id",
        entityColumn = "flight_id"
    )
    val flightInfo: FlightInfo
)