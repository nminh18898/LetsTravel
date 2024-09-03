package com.minhhnn18898.manage_trip.trip_detail.utils

import com.google.common.truth.Truth
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo

fun assertFlightAndAirportEqual(listExpected: List<FlightWithAirportInfo>, listTarget: List<FlightWithAirportInfo?>) {
    Truth.assertThat(listExpected).hasSize(listTarget.size)

    for(i in listExpected.indices) {
        val expected = listExpected[i]
        val target = listTarget[i]
        assertFlightAndAirportEqual(expected = expected, target = target)
    }
}

fun assertFlightAndAirportEqual(expected: FlightWithAirportInfo, target: FlightWithAirportInfo?) {
    Truth.assertThat(target).isNotNull()
    assertFlightInfoEqual(expected.flightInfo, target?.flightInfo)
    assertAirportInfoEqual(expected.departAirport, target?.departAirport)
    assertAirportInfoEqual(expected.destinationAirport, target?.destinationAirport)
}

fun assertFlightInfoEqual(expected: FlightInfo, target: FlightInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.flightNumber).isEqualTo(expected.flightNumber)
    Truth.assertThat(target?.operatedAirlines).isEqualTo(expected.operatedAirlines)
    Truth.assertThat(target?.departureTime).isEqualTo(expected.departureTime)
    Truth.assertThat(target?.arrivalTime).isEqualTo(expected.arrivalTime)
    Truth.assertThat(target?.price).isEqualTo(expected.price)
}

fun assertAirportInfoEqual(expected: AirportInfo, target: AirportInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.code).isEqualTo(expected.code)
    Truth.assertThat(target?.city).isEqualTo(expected.city)
    Truth.assertThat(target?.airportName).isEqualTo(expected.airportName)
}

fun assertHotelInfoEqual(expected: HotelInfo, target: HotelInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.hotelName).isEqualTo(expected.hotelName)
    Truth.assertThat(target?.address).isEqualTo(expected.address)
    Truth.assertThat(target?.checkInDate).isEqualTo(expected.checkInDate)
    Truth.assertThat(target?.checkOutDate).isEqualTo(expected.checkOutDate)
    Truth.assertThat(target?.price).isEqualTo(expected.price)
}

fun assertActivityInfoEqual(expected: TripActivityInfo, target: TripActivityInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.title).isEqualTo(expected.title)
    Truth.assertThat(target?.description).isEqualTo(expected.description)
    Truth.assertThat(target?.photo).isEqualTo(expected.photo)
    Truth.assertThat(target?.timeFrom).isEqualTo(expected.timeFrom)
    Truth.assertThat(target?.timeTo).isEqualTo(expected.timeTo)
    Truth.assertThat(target?.price).isEqualTo(expected.price)
}