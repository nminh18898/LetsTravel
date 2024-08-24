package com.minhhnn18898.letstravel.tripdetail.presentation.trip

data class FlightDisplayInfo(
    val flightId: Long,
    val flightNumber: String,
    val departAirport: AirportDisplayInfo,
    val destinationAirport: AirportDisplayInfo,
    val operatedAirlines: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val price: String
)

data class AirportDisplayInfo(
    val city: String,
    val code: String,
    val airportName: String = "",
)

data class HotelDisplayInfo(
    val hotelId: Long,
    val hotelName: String,
    val address: String,
    val checkInDate: String,
    val checkOutDate: String,
    val duration: Int,
    val price: String,
)

data class TripActivityDisplayInfo(
    val activityId: Long,
    val title: String,
    val description: String,
    val photo: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val price: String,
)