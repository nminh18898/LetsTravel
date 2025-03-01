package com.minhhnn18898.discover.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ArticlePreviewModel(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    @get:PropertyName("thumb_url") @set:PropertyName("thumb_url")
    var thumbUrl: String = "",
    @get:PropertyName("last_edited") @set:PropertyName("last_edited")
    @ServerTimestamp
    var lastEdited: Date? = null
)