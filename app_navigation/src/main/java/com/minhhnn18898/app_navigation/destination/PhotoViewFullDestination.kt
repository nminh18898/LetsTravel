package com.minhhnn18898.app_navigation.destination

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PhotoViewFullDestination(val param: PhotoViewFullParam)

@Serializable
@Parcelize
data class PhotoViewFullParam(
    val url: String
): Parcelable

val PhotoViewFullParamType = object : NavType<PhotoViewFullParam>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): PhotoViewFullParam? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, PhotoViewFullParam::class.java)
        } else {
            @Suppress("DEPRECATION") // for backwards compatibility
            bundle.getParcelable(key)
        }

    override fun put(bundle: Bundle, key: String, value: PhotoViewFullParam) =
        bundle.putParcelable(key, value)

    override fun parseValue(value: String): PhotoViewFullParam = Json.decodeFromString<PhotoViewFullParam>(value)

    override fun serializeAsValue(value: PhotoViewFullParam): String = Json.encodeToString(value)

}