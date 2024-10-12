package com.minhhnn18898.discover.presentation.ui_models

import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.discover.data.model.Article
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class ArticleDisplayInfo(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    var thumbUrl: String = "",
    var photoUrls: List<String> = emptyList(),
    var lastEdited: String = "",
    var originalSrc: String = "",
    val tag: ArrayList<String> = ArrayList()
)

fun Article.toDisplayInfo(baseDateTimeFormatter: BaseDateTimeFormatter): ArticleDisplayInfo {
    return ArticleDisplayInfo(
        id = this.id,
        title = this.title,
        content = this.content,
        thumbUrl = this.thumbUrl,
        photoUrls = this.photoUrls,
        lastEdited = baseDateTimeFormatter.dateToFormattedString(this.lastEdited ?: Date(), DateTimeFormatter.ofPattern("EEE, dd MMMM, yyyy", Locale.getDefault())),
        originalSrc = this.originalSrc,
        tag = this.tag
    )
}