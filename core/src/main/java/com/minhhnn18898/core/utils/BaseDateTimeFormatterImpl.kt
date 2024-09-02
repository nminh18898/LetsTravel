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
open class BaseDateTimeFormatterImpl @Inject constructor() : BaseDateTimeFormatter {
    override fun millisToLocalDate(millis: Long): LocalDate {
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


    override fun dateToFormattedString(date: LocalDate, formatter: DateTimeFormatter): String {
        val localDate = convertMillisToLocalDateWithFormatter(date, formatter)
        return formatter.format(localDate)
    }

    override fun dateToFormattedString(date: Date, formatter: DateTimeFormatter): String {
        val localDate = millisToLocalDate(date.time)
        return formatter.format(localDate)
    }

    override fun millisToFormattedString(millis: Long, formatter: DateTimeFormatter): String {
        return dateToFormattedString(millisToLocalDate(millis), formatter)
    }

    override fun formatHourMinute(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    override fun millisToLocalDateTime(millis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), TimeZone.getDefault().toZoneId())
    }

    override fun combineDateTimeToMillis(dateMillis: Long, hour: Int, minute: Int): Long {
        val localDate = millisToLocalDate(dateMillis)
        val localTime = LocalTime.of(hour, minute)
        return LocalDateTime.of(localDate, localTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    override fun findDurationInHourMinute(from: Long, to: Long): Pair<Int, Int> {
        val duration = (to - from).toDuration(DurationUnit.MILLISECONDS)
        return Pair(duration.inWholeHours.toInt(), duration.inWholeMinutes.rem(60).toInt())
    }

    override fun findDurationInHourMinuteFormattedString(from: Long, to: Long): String {
        val duration = findDurationInHourMinute(from, to)
        return formatHourMinute(duration.first, duration.second)
    }

    override fun getHourMinute(time: String): Pair<Int, Int> {
        val localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
        return Pair(localTime.hour, localTime.minute)
    }

    override fun getHourMinute(millis: Long): Pair<Int, Int> {
        val localDateTime = millisToLocalDateTime(millis)
        return Pair(localDateTime.hour, localDateTime.minute)
    }

    override fun getStartOfTheDay(millis: Long): Long {
        return millisToLocalDate(millis)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}