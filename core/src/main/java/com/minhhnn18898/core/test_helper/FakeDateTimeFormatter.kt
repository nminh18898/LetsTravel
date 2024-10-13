package com.minhhnn18898.core.test_helper

import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class FakeDateTimeFormatter: BaseDateTimeFormatter {
    override fun millisToLocalDate(millis: Long): LocalDate {
        TODO("Not yet implemented")
    }

    override fun millisToLocalDateTime(millis: Long): LocalDateTime {
        TODO("Not yet implemented")
    }

    override fun millisToFormattedString(millis: Long, formatter: DateTimeFormatter): String {
        TODO("Not yet implemented")
    }

    override fun getStartOfTheDay(millis: Long): Long {
        TODO("Not yet implemented")
    }

    override fun dateToFormattedString(date: LocalDate, formatter: DateTimeFormatter): String {
        TODO("Not yet implemented")
    }

    override fun dateToFormattedString(date: Date, formatter: DateTimeFormatter): String {
        return date.time.toString()
    }

    override fun formatHourMinute(hour: Int, minute: Int): String {
        TODO("Not yet implemented")
    }

    override fun getHourMinute(time: String): Pair<Int, Int> {
        TODO("Not yet implemented")
    }

    override fun getHourMinute(millis: Long): Pair<Int, Int> {
        TODO("Not yet implemented")
    }

    override fun findDurationInHourMinute(from: Long, to: Long): Pair<Int, Int> {
        TODO("Not yet implemented")
    }

    override fun findDurationInHourMinuteFormattedString(from: Long, to: Long): String {
        TODO("Not yet implemented")
    }

    override fun combineDateTimeToMillis(dateMillis: Long, hour: Int, minute: Int): Long {
        TODO("Not yet implemented")
    }
}