package com.minhhnn18898.discover.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ArticleModel(
    val title: String = "",
    val content: String = "",
    @get:PropertyName("thumb_url") @set:PropertyName("thumb_url")
    var thumbUrl: String = "",
    @get:PropertyName("photo_urls") @set:PropertyName("photo_urls")
    var photoUrls: List<String> = emptyList(),
    @get:PropertyName("last_edited") @set:PropertyName("last_edited")
    @ServerTimestamp
    var lastEdited: Date? = null,
    val region: String = "",
    val country: String = "",
    @get:PropertyName("original_src") @set:PropertyName("original_src")
    var originalSrc: String = "",
    val tag: ArrayList<String> = ArrayList()
)