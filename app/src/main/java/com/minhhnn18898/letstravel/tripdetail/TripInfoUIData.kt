package com.minhhnn18898.letstravel.tripdetail

data class FlightInfo(
    val flightNumber: String,
    val departAirport: AirportInfo,
    val destinationAirport: AirportInfo,
    val operatedAirlines: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val price: String
)

data class AirportInfo(
    val city: String,
    val code: String,
    val airportName: String = "",
)

data class HotelInfo(
    val hotelName: String,
    val address: String,
    val checkInDate: String,
    val checkOutDate: String,
    val duration: String,
    val price: String,
)