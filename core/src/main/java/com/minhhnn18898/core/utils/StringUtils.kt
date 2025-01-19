package com.minhhnn18898.core.utils

import android.content.Context
import androidx.annotation.StringRes
import java.text.NumberFormat
import java.util.Locale
import java.util.regex.Pattern

object StringUtils {

    fun getString(context: Context, @StringRes id: Int): String {
        runCatching {
            return context.getString(id)
        }.onFailure {
            return ""
        }

        return ""
    }

    fun getRequiredFieldIndicator(): String = "(*)"
}

fun String.isNotBlankOrEmpty(): Boolean = this.isNotBlank() && this.isNotEmpty()

fun String.isValidEmail(): Boolean {
    val emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    return emailPattern.matcher(this).matches()
}

fun Long?.formatWithCommas(): String =
    NumberFormat.getNumberInstance(Locale.US).format(this ?: 0)

fun Long.toCompactString(locale: Locale = Locale.US, numberOfFloatingPoint: Int = 1): String {
    return when {
        this >= 1_000_000 -> String.format(locale, "%.${numberOfFloatingPoint}fM", this / 1_000_000.0)
        this >= 1_000 -> String.format(locale, "%.${numberOfFloatingPoint}fK", this / 1_000.0)
        else -> this.toString()
    }.replace('.', ',') // Replace the default dot with a comma for decimal separator
}