package com.minhhnn18898.discover.data.repo

import com.minhhnn18898.discover.data.model.Article

interface DiscoverRepository {
    suspend fun getArticles(): List<Article>
    suspend fun getArticle(id: String): Article?
}

class ExceptionGetDiscoverArticle: Exception()