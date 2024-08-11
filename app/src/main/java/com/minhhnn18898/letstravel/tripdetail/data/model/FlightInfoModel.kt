package com.minhhnn18898.letstravel.tripdetail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_info")
data class FlightInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("flight_id")
    val flightId: Int,
    @ColumnInfo("flight_number")
    val flightNumber: String,
    @ColumnInfo("operated_airlines")
    val operatedAirlines: String,
    @ColumnInfo("departure_time")
    val departureTime: Long,
    @ColumnInfo("arrival_time")
    val arrivalTime: Long,
    @ColumnInfo("price")
    val price: Long,

    // Relation mapping
    @ColumnInfo("trip_id")
    val tripId: Long,
    @ColumnInfo("depart_airport_code")
    val departAirportCode: String,
    @ColumnInfo("destination_airport_code")
    val destinationAirportCode: String
)

fun FlightInfoModel.toFlightInfo(): FlightInfo {
    return FlightInfo(
        this.flightNumber,
        this.operatedAirlines,
        this.departureTime,
        this.arrivalTime,
        this.price
    )
}

data class FlightInfo(
    val flightNumber: String,
    val operatedAirlines: String,
    val departureTime: Long,
    val arrivalTime: Long,
    val price: Long
)

data class FlightWithAirportInfo(
    val flightInfo: FlightInfo,
    val departAirport: AirportInfoModel,
    val destinationAirport: AirportInfoModel
)