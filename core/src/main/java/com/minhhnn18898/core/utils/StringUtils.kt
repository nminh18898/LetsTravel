package com.minhhnn18898.core.utils

import android.content.Context
import androidx.annotation.StringRes
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

fun Long.formatWithCommas(): String {
    return "%,d".format(this)
}