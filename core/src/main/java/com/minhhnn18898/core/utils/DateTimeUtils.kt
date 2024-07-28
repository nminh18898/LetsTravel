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
class DateTimeUtils @Inject constructor() {

    private val defaultFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())

    fun convertMillisToLocalDate(millis: Long): LocalDate {
        return Instant
            .ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private fun convertMillisToLocalDateWithFormatter(date: LocalDate, dateTimeFormatter: DateTimeFormatter): LocalDate {
        //Convert the date to a long in millis using a dateformmater
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


    fun dateToString(date: LocalDate): String {
        val localDate = convertMillisToLocalDateWithFormatter(date, defaultFormatter)
        return defaultFormatter.format(localDate)
    }

    fun dateToString(date: Date): String {
        val localDate = convertMillisToLocalDate(date.time)
        return defaultFormatter.format(localDate)
    }

    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun getFormatDateTimeString(millis: Long): String {
        val localDateTime = convertMillisToLocalDateTime(millis)
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm\nEEEE, dd MMMM", Locale.getDefault())
        return dateFormatter.format(localDateTime)
    }

    private fun convertMillisToLocalDateTime(millis: Long): LocalDateTime {
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

    private fun getDurationInHourMinute(from: Long, to: Long): Pair<Int, Int> {
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
}