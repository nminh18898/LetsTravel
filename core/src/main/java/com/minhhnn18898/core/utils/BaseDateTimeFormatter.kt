package com.minhhnn18898.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

interface BaseDateTimeFormatter {
    fun millisToLocalDate(millis: Long): LocalDate
    fun millisToLocalDateTime(millis: Long): LocalDateTime
    fun millisToFormattedString(millis: Long, formatter: DateTimeFormatter): String
    fun getStartOfTheDay(millis: Long): Long

    fun dateToFormattedString(date: LocalDate, formatter: DateTimeFormatter): String
    fun dateToFormattedString(date: Date, formatter: DateTimeFormatter): String

    fun formatHourMinute(hour: Int, minute: Int): String
    fun getHourMinute(time: String): Pair<Int, Int>
    fun getHourMinute(millis: Long): Pair<Int, Int>
    fun findDurationInHourMinute(from: Long, to: Long): Pair<Int, Int>
    fun findDurationInHourMinuteFormattedString(from: Long, to: Long): String
    fun combineDateTimeToMillis(dateMillis: Long, hour: Int, minute: Int): Long
}