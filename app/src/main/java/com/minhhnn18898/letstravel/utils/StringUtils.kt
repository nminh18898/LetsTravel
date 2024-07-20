package com.minhhnn18898.letstravel.utils

import android.content.Context
import androidx.annotation.StringRes

object StringUtils {

    fun getString(context: Context, @StringRes id: Int): String {
        return context.getString(id)
    }
}