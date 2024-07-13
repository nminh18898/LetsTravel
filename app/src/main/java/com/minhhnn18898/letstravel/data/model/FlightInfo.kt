package com.minhhnn18898.letstravel.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "flight_info")
data class FlightInfo(
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
    val price: Long
)

@Entity(tableName = "flight_with_airport_info_ref")
data class FlightWithAirportInfoMapping(
    @PrimaryKey
    @ColumnInfo("flight_id")
    val flightId: Int,
    @ColumnInfo("depart_airport_code")
    val departAirportCode: String,
    @ColumnInfo("destination_airport_code")
    val destinationAirportCode: String
)

data class FlightInfoWithAirport(
    @Embedded val flightInfo: FlightInfo,
    @Relation(
        parentColumn = "flight_id",
        entityColumn = "depart_airport_code",
        associateBy = Junction(FlightWithAirportInfoMapping::class)
    )
    val airportInfo: FlightWithAirportInfoMapping,
    /*@Relation(
        parentColumn = "flight_id",
        entityColumn = "destination_airport_code",
        associateBy = Junction(FlightWithAirportInfoMapping::class)
    )
    val arrivalAirport: AirportInfo*/
)