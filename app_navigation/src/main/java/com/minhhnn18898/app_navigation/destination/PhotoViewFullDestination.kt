package com.minhhnn18898.app_navigation.destination

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class PhotoViewFullDestination(val param: PhotoViewFullParam)

@Serializable
@Parcelize
data class PhotoViewFullParam(
    val url: String
): Parcelable