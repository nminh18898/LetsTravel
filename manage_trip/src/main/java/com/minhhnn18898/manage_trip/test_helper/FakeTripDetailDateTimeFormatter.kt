package com.minhhnn18898.manage_trip.test_helper

import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import java.time.LocalDate

class FakeTripDetailDateTimeFormatter: TripDetailDateTimeFormatter {
    override fun getActivityFormattedDateSeparatorString(millis: Long): String {
        return millis.toString()
    }

    override fun findFlightDurationFormattedString(from: Long, to: Long): String {
        return (to - from).toString()
    }

    override fun getHotelNights(from: Long, to: Long): Long {
        return to - from
    }

    override fun getFormattedFlightDateTimeString(millis: Long): String {
        return millis.toString()
    }

    override fun getFormattedFlightDateString(localDate: LocalDate): String {
        return (localDate.year * 100 + localDate.monthValue * 10 + localDate.dayOfYear).toString()
    }

    override fun millisToHotelFormattedString(millis: Long): String {
        return millis.toString()
    }

    override fun millisToActivityFormattedString(millis: Long): String {
        return millis.toString()
    }

    override fun formatHourMinutes(millis: Long): String {
        return millis.toString()
    }

    override fun getHourMinute(millis: Long): Pair<Int, Int> {
        return Pair(millis.toString().first().digitToInt(), millis.toString().last().digitToInt())
    }

    override fun combineHourMinutesDayToMillis(dateMillis: Long, hour: Int, minute: Int): Long {
        return dateMillis * 10 + hour * 5 + minute
    }

    override fun getStartOfTheDay(millis: Long): Long {
        return millis.toString().last().digitToInt().toLong()
    }
}