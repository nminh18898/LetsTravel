package com.minhhnn18898.discover.data

import com.minhhnn18898.discover.data.model.Article

interface DiscoverRepository {
    suspend fun getArticles(): List<Article>
}

class ExceptionGetDiscoverArticles: Exception()