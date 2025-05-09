package com.minhhnn18898.manage_trip.trip_detail.utils

import com.google.common.truth.Truth
import com.minhhnn18898.trip_data.model.plan.AirportInfo
import com.minhhnn18898.trip_data.model.plan.FlightInfo
import com.minhhnn18898.trip_data.model.plan.FlightWithAirportInfo
import com.minhhnn18898.trip_data.model.plan.HotelInfo
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo

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

fun assertHotelInfoEqual(listExpected: List<HotelInfo>, listTarget: List<HotelInfo?>) {
    Truth.assertThat(listExpected).hasSize(listTarget.size)

    for(i in listExpected.indices) {
        val expected = listExpected[i]
        val target = listTarget[i]
        assertHotelInfoEqual(expected = expected, target = target)
    }
}

fun assertHotelInfoEqual(expected: HotelInfo, target: HotelInfo?) {
    Truth.assertThat(target).isNotNull()
    Truth.assertThat(target?.hotelName).isEqualTo(expected.hotelName)
    Truth.assertThat(target?.address).isEqualTo(expected.address)
    Truth.assertThat(target?.checkInDate).isEqualTo(expected.checkInDate)
    Truth.assertThat(target?.checkOutDate).isEqualTo(expected.checkOutDate)
    Truth.assertThat(target?.price).isEqualTo(expected.price)
}

@Suppress("unused")
fun assertActivityInfoEqual(listExpected: List<TripActivityInfo>, listTarget: List<TripActivityInfo?>) {
    Truth.assertThat(listExpected).hasSize(listTarget.size)

    for(i in listExpected.indices) {
        val expected = listExpected[i]
        val target = listTarget[i]
        assertActivityInfoEqual(expected = expected, target = target)
    }
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