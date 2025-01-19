package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import java.time.LocalDate

interface TripDetailDateTimeFormatter {
    fun getActivityFormattedDateSeparatorString(millis: Long): String
    fun getReceiptFormattedDateSeparatorString(millis: Long): String
    fun findFlightDurationFormattedString(from: Long, to: Long): String
    fun getHotelNights(from: Long, to: Long): Long
    fun getFormattedFlightDateTimeString(millis: Long): String
    fun getFormattedFlightDateString(localDate: LocalDate): String
    fun millisToHotelFormattedString(millis: Long): String
    fun millisToActivityFormattedString(millis: Long): String
    fun formatHourMinutes(millis: Long): String
    fun getHourMinute(millis: Long): Pair<Int, Int>
    fun combineHourMinutesDayToMillis(dateMillis: Long, hour: Int, minute: Int): Long
    fun getStartOfTheDay(millis: Long): Long
    fun getFormattedReceiptCreatedDate(millis: Long): String
}