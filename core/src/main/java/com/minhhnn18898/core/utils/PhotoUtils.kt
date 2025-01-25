package com.minhhnn18898.core.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.WorkerThread

@WorkerThread
fun Uri.imageSize(context: Context): Pair<Int, Int>{
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    context.contentResolver.openInputStream(this).use {
        BitmapFactory.decodeStream(it, null, options)
        return Pair(options.outWidth, options.outHeight)
    }
}