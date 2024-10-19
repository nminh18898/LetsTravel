package com.minhhnn18898.app_navigation.destination

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.minhhnn18898.core.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DiscoveryArticleDetailScreenDestination(val parameters: DiscoveryArticleDetailScreenParameters) {
    companion object {
        val title: Int =  R.string.article
    }
}

@Serializable
@Parcelize
data class DiscoveryArticleDetailScreenParameters(
    val articleId: String,
    val articlePosition: Int,
    val listArticles: List<String>
): Parcelable

val DiscoveryArticleDetailScreenParametersType = object : NavType<DiscoveryArticleDetailScreenParameters>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): DiscoveryArticleDetailScreenParameters? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, DiscoveryArticleDetailScreenParameters::class.java)
        } else {
            @Suppress("DEPRECATION") // for backwards compatibility
            bundle.getParcelable(key)
        }

    override fun put(bundle: Bundle, key: String, value: DiscoveryArticleDetailScreenParameters) =
        bundle.putParcelable(key, value)

    override fun parseValue(value: String): DiscoveryArticleDetailScreenParameters = Json.decodeFromString<DiscoveryArticleDetailScreenParameters>(value)

    override fun serializeAsValue(value: DiscoveryArticleDetailScreenParameters): String = Json.encodeToString(value)

}