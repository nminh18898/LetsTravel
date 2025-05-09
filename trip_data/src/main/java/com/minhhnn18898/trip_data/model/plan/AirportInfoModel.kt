package com.minhhnn18898.trip_data.model.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("airport_info")
data class AirportInfoModel(
    @PrimaryKey
    @ColumnInfo("airport_code")
    val code: String,
    val city: String,
    @ColumnInfo("airport_name")
    val airportName: String = ""
)

data class AirportInfo(
    val code: String = "",
    val city: String = "",
    val airportName: String = ""
)

fun AirportInfoModel.toAirportInfo(): AirportInfo {
    return AirportInfo(
        code = this.code,
        city = this.city,
        airportName = this.airportName
    )
}

fun AirportInfo.toAirportInfoModel(): AirportInfoModel {
    return AirportInfoModel(
        code = this.code,
        city = this.city,
        airportName = this.airportName
    )
}