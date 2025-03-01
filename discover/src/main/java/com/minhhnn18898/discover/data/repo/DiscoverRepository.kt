package com.minhhnn18898.discover.data.repo

import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.data.model.ArticlePreview

interface DiscoverRepository {
    suspend fun getArticlesPreview(): List<ArticlePreview>
    suspend fun getArticle(id: String): Article?
}

class ExceptionGetDiscoverArticle: Exception()