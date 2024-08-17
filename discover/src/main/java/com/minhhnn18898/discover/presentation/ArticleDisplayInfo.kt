package com.minhhnn18898.discover.presentation

data class ArticleDisplayInfo(
    val title: String = "",
    val content: String = "",
    var thumbUrl: String = "",
    var photoUrls: List<String> = emptyList(),
    var lastEdited: String = "",
    var originalSrc: String = "",
    val tag: ArrayList<String> = ArrayList()
)