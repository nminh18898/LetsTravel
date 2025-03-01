package com.minhhnn18898.discover.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Article(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    var thumbUrl: String = "",
    var photoUrls: List<String> = emptyList(),
    @ServerTimestamp
    var lastEdited: Date? = null,
    var originalSrc: String = "",
    val tag: List<String> = ArrayList()
)

fun Article.toArticlePreview(): ArticlePreview {
    return ArticlePreview(
        id = this.id,
        title = this.title,
        content = this.content,
        thumbUrl = this.thumbUrl,
        lastEdited = this.lastEdited
    )
}