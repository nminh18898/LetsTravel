package com.minhhnn18898.core.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Suppress("unused")
open class BaseDateTimeFormatter @Inject constructor() {

    private val defaultFormatter = DateTimeFormatter.ofPattern("EEE, dd MMMM, yyyy", Locale.getDefault())

    private val dateMonthYearShortFormatter = DateTimeFormatter.ofPattern("dd MMMM, yy", Locale.getDefault())

    fun convertMillisToLocalDate(millis: Long): LocalDate {
        return Instant
            .ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private fun convertMillisToLocalDateWithFormatter(date: LocalDate, dateTimeFormatter: DateTimeFormatter): LocalDate {
        //Convert the date to a long in millis using a date formatter
        val dateInMillis = LocalDate.parse(date.format(dateTimeFormatter), dateTimeFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        //Convert the millis to a localDate object
        return Instant
            .ofEpochMilli(dateInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }


    fun dateToString(date: LocalDate, formatter: DateTimeFormatter = defaultFormatter): String {
        val localDate = convertMillisToLocalDateWithFormatter(date, formatter)
        return formatter.format(localDate)
    }

    fun dateToString(date: Date, formatter: DateTimeFormatter = dateMonthYearShortFormatter): String {
        val localDate = convertMillisToLocalDate(date.time)
        return formatter.format(localDate)
    }

    fun millisToDateString(millis: Long, formatter: DateTimeFormatter = dateMonthYearShortFormatter): String {
        return dateToString(convertMillisToLocalDate(millis), formatter)
    }

    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun getFormatFlightDateTimeString(millis: Long): String {
        val localDateTime = convertMillisToLocalDateTime(millis)
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm\nEEE, dd MMMM", Locale.getDefault())
        return dateFormatter.format(localDateTime)
    }

    fun convertMillisToLocalDateTime(millis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), TimeZone.getDefault().toZoneId())
    }

    fun convertToLocalDateTimeMillis(dateMillis: Long, hour: Int, minute: Int): Long {
        val localDate = convertMillisToLocalDate(dateMillis)
        val localTime = LocalTime.of(hour, minute)
        return LocalDateTime.of(localDate, localTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun getDurationInHourMinute(from: Long, to: Long): Pair<Int, Int> {
        val duration = (to - from).toDuration(DurationUnit.MILLISECONDS)
        return Pair(duration.inWholeHours.toInt(), duration.inWholeMinutes.rem(60).toInt())
    }

    fun getDurationInHourMinuteDisplayString(from: Long, to: Long): String {
        val duration = getDurationInHourMinute(from, to)
        return formatTime(duration.first, duration.second)
    }

    fun parseHourMinute(time: String): Pair<Int, Int> {
        val localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
        return Pair(localTime.hour, localTime.minute)
    }

    fun getHourMinute(millis: Long): Pair<Int, Int> {
        val localDateTime = convertMillisToLocalDateTime(millis)
        return Pair(localDateTime.hour, localDateTime.minute)
    }

    fun getNightDuration(from: Long, to: Long): Long {
        val duration = (to - from).toDuration(DurationUnit.MILLISECONDS)
        return duration.inWholeDays
    }

    fun getStartOfDayInMillis(millis: Long): Long {
        return convertMillisToLocalDate(millis)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}