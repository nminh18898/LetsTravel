package com.minhhnn18898.trip_data.model.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel

@Entity(
    tableName = "flight_info",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FlightInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("flight_id")
    val flightId: Long,
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
        flightId = this.flightId,
        flightNumber = this.flightNumber,
        operatedAirlines = this.operatedAirlines,
        departureTime = this.departureTime,
        arrivalTime = this.arrivalTime,
        price = this.price
    )
}

data class FlightInfo(
    val flightId: Long = 0L,
    val flightNumber: String = "",
    val operatedAirlines: String = "",
    val departureTime: Long = 0L,
    val arrivalTime: Long = 0L,
    val price: Long = 0L
)

data class FlightWithAirportInfo(
    val flightInfo: FlightInfo,
    val departAirport: AirportInfo,
    val destinationAirport: AirportInfo
)

fun FlightInfo.toFlightInfoModel(tripId: Long, departAirportCode: String, destinationAirportCode: String): FlightInfoModel {
    return FlightInfoModel(
        flightId = this.flightId,
        flightNumber = this.flightNumber,
        operatedAirlines = this.operatedAirlines,
        departureTime = this.departureTime,
        arrivalTime = this.arrivalTime,
        price = this.price,
        tripId = tripId,
        departAirportCode = departAirportCode,
        destinationAirportCode = destinationAirportCode
    )
}