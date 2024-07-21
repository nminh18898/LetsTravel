package com.minhhnn18898.letstravel.tripdetail.data

import com.minhhnn18898.letstravel.tripdetail.ui.trip.AirportDisplayInfo
import com.minhhnn18898.letstravel.tripdetail.ui.trip.FlightDisplayInfo
import com.minhhnn18898.letstravel.tripdetail.ui.trip.HotelDisplayInfo

object MockDataProvider {

    fun provideFlightInfo(): List<FlightDisplayInfo> {
        return mutableListOf<FlightDisplayInfo>().apply {
            add(
                FlightDisplayInfo(
                flightNumber = "VN 939",
                departAirport = AirportDisplayInfo("Ho Chi Minh", "SGN", "Tan Son Nhat"),
                destinationAirport = AirportDisplayInfo("Chiang Mai", "CNX", "Chiang Mai"),
                operatedAirlines = "Vietnam Airlines",
                departureTime = "08:00, 20 May 2024",
                arrivalTime =  "10:00, 20 May 2024",
                duration = "02:00",
                price = "1.200.000"
            )
            )

            add(
                FlightDisplayInfo(
                flightNumber = "TG 557",
                departAirport =  AirportDisplayInfo("Chiang Mai", "CNX", "Chiang Mai"),
                destinationAirport = AirportDisplayInfo("Bangkok", "BKK", "Suvarnabhumi"),
                operatedAirlines = "Thai Airways",
                departureTime = "20:00, 23 May 2024",
                arrivalTime =  "21:10, 23 May 2024",
                duration = "1:10",
                price = "900.000"
            )
            )

            add(
                FlightDisplayInfo(
                flightNumber = "TG 557",
                departAirport =  AirportDisplayInfo("Bangkok ", "BKK", "Suvarnabhumi"),
                destinationAirport = AirportDisplayInfo("Ho Chi Minh ", "SGN", "Tan Son Nhat"),
                operatedAirlines = "Thai Airways",
                departureTime = "21:00, 25 May 2024",
                arrivalTime =  "23:25, 25 May 2024",
                duration = "2:25",
                price = "1357.400"
            )
            )
        }
    }

    fun provideHotelInfo(): List<HotelDisplayInfo> {
        return mutableListOf<HotelDisplayInfo>().apply {
            add(
                HotelDisplayInfo(
                hotelName = "Travelodge Nimman",
                address = "Chiang Mai, Thailand",
                checkInDate = "20 May, 2024",
                checkOutDate = "23 May, 2024",
                duration = "3 nights",
                price = "2.400.000"

            )
            )

            add(
                HotelDisplayInfo(
                hotelName = "Amber Inn",
                address = "Pratunam, Bangkok, Thailand",
                checkInDate = "23 May, 2024",
                checkOutDate = "25 May, 2024",
                duration = "2 nights",
                price = "1.600.000"

            )
            )
        }
    }
}