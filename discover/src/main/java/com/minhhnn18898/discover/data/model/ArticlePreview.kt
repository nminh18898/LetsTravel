package com.minhhnn18898.discover.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ArticlePreview(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    var thumbUrl: String = "",
    @ServerTimestamp
    var lastEdited: Date? = null
)