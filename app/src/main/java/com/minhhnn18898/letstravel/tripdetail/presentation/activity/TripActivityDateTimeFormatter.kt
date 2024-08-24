package com.minhhnn18898.letstravel.tripdetail.presentation.activity

import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject


@Suppress("unused")
open class TripActivityDateTimeFormatter @Inject constructor(private val baseDateTimeFormatter: BaseDateTimeFormatter) {

    private val defaultFormatter = DateTimeFormatter.ofPattern("EE, dd MMM, yyyy", Locale.getDefault())

    fun getHourMinuteFormatted(millis: Long):String {
        val localDateTime = baseDateTimeFormatter.convertMillisToLocalDateTime(millis)
        return baseDateTimeFormatter.formatTime(localDateTime.hour, localDateTime.minute)
    }

    fun millisToDateString(millis: Long): String {
        return baseDateTimeFormatter.millisToDateString(millis, defaultFormatter)
    }

    fun convertToLocalDateTimeMillis(dateMillis: Long, hour: Int, minute: Int): Long {
        return baseDateTimeFormatter.convertToLocalDateTimeMillis(dateMillis, hour, minute)
    }

    fun getHourMinute(millis: Long): Pair<Int, Int> {
        return baseDateTimeFormatter.getHourMinute(millis)
    }
}