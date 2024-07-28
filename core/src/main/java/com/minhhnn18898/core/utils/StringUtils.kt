package com.minhhnn18898.core.utils

import android.content.Context
import androidx.annotation.StringRes

object StringUtils {

    fun getString(context: Context, @StringRes id: Int): String {
        return context.getString(id)
    }

    fun getRequiredFieldIndicator(): String = "(*)"
}

fun String.isNotBlankOrEmpty(): Boolean = this.isNotBlank() && this.isNotEmpty()