package com.minhhnn18898.discover.presentation.ui_models

import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.discover.data.model.ArticlePreview
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class ArticlePreviewDisplayInfo(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    var thumbUrl: String = "",
    var lastEdited: String = ""
)

fun ArticlePreview.toDisplayInfo(baseDateTimeFormatter: BaseDateTimeFormatter): ArticlePreviewDisplayInfo {
    return ArticlePreviewDisplayInfo(
        id = this.id,
        title = this.title,
        content = this.content,
        thumbUrl = this.thumbUrl,
        lastEdited = baseDateTimeFormatter.dateToFormattedString(this.lastEdited ?: Date(), DateTimeFormatter.ofPattern("EEE, dd MMMM, yyyy", Locale.getDefault()))
    )
}