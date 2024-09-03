package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class TripDetailDateTimeFormatterImpl @Inject constructor(
    private val baseDateTimeFormatter: BaseDateTimeFormatter
) : TripDetailDateTimeFormatter {

    private val hotelFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM, yy", Locale.getDefault())

    private val activityFormatter = DateTimeFormatter.ofPattern("EE, dd MMM, yyyy", Locale.getDefault())
    private val activityDateSeparatorFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())

    private val flightDateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm\nEEE, dd MMMM", Locale.getDefault())
    private val flightDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMMM, yyyy", Locale.getDefault())

    override fun getActivityFormattedDateSeparatorString(millis: Long): String {
        return baseDateTimeFormatter.millisToFormattedString(millis, activityDateSeparatorFormatter)
    }

    override fun findFlightDurationFormattedString(from: Long, to: Long): String {
        return baseDateTimeFormatter.findDurationInHourMinuteFormattedString(from, to)
    }

    override fun getHotelNights(from: Long, to: Long): Long {
        val duration = (to - from).toDuration(DurationUnit.MILLISECONDS)
        return duration.inWholeDays
    }

    override fun getFormattedFlightDateTimeString(millis: Long): String {
        val localDateTime = baseDateTimeFormatter.millisToLocalDateTime(millis)
        return flightDateTimeFormatter.format(localDateTime)
    }

    override fun getFormattedFlightDateString(localDate: LocalDate): String {
        return baseDateTimeFormatter.dateToFormattedString(localDate, flightDateFormatter)
    }

    override fun millisToHotelFormattedString(millis: Long): String {
        return baseDateTimeFormatter.millisToFormattedString(millis, hotelFormatter)
    }

    override fun millisToActivityFormattedString(millis: Long): String {
        return baseDateTimeFormatter.millisToFormattedString(millis, activityFormatter)
    }

    override fun formatHourMinutes(millis: Long):String {
        val localDateTime = baseDateTimeFormatter.millisToLocalDateTime(millis)
        return baseDateTimeFormatter.formatHourMinute(localDateTime.hour, localDateTime.minute)
    }

    override fun getHourMinute(millis: Long): Pair<Int, Int> {
        return baseDateTimeFormatter.getHourMinute(millis)
    }

    override fun combineActivityDateTimeToMillis(dateMillis: Long, hour: Int, minute: Int): Long {
        return baseDateTimeFormatter.combineDateTimeToMillis(dateMillis, hour, minute)
    }

    override fun getStartOfTheDay(millis: Long): Long {
        return baseDateTimeFormatter.getStartOfTheDay(millis)
    }
}