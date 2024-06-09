package com.minhhnn18898.letstravel.data

import com.minhhnn18898.letstravel.tripdetail.AirportInfo
import com.minhhnn18898.letstravel.tripdetail.FlightInfo

object MockDataProvider {

    fun provideFlightInfo(): List<FlightInfo> {
        return mutableListOf<FlightInfo>().apply {
            add(FlightInfo(
                flightNumber = "VN 939",
                departAirport = AirportInfo("Ho Chi Minh", "SGN", "Tan Son Nhat"),
                destinationAirport = AirportInfo("Chiang Mai", "CNX", "Chiang Mai"),
                operatedAirlines = "Vietnam Airlines",
                departureTime = "08:00, 20 May 2024",
                arrivalTime =  "10:00, 20 May 2024",
                duration = "02:00",
                price = "1.200.000"
            ))

            add(FlightInfo(
                flightNumber = "TG 557",
                departAirport =  AirportInfo("Chiang Mai", "CNX", "Chiang Mai"),
                destinationAirport = AirportInfo("Bangkok", "BKK", "Suvarnabhumi"),
                operatedAirlines = "Thai Airways",
                departureTime = "20:00, 23 May 2024",
                arrivalTime =  "21:10, 23 May 2024",
                duration = "1:10",
                price = "900.000"
            ))

            add(FlightInfo(
                flightNumber = "TG 557",
                departAirport =  AirportInfo("Bangkok ", "BKK", "Suvarnabhumi"),
                destinationAirport = AirportInfo("Ho Chi Minh ", "SGN", "Tan Son Nhat"),
                operatedAirlines = "Thai Airways",
                departureTime = "21:00, 25 May 2024",
                arrivalTime =  "23:25, 25 May 2024",
                duration = "2:25",
                price = "1357.400"
            ))
        }
    }
}