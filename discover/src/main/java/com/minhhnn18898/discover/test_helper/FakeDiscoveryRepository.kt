@file:Suppress("SpellCheckingInspection")

package com.minhhnn18898.discover.test_helper

import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.data.model.ArticlePreview
import com.minhhnn18898.discover.data.model.toArticlePreview
import com.minhhnn18898.discover.data.repo.DiscoverRepository
import com.minhhnn18898.discover.data.repo.ExceptionGetDiscoverArticle

class FakeDiscoveryRepository: DiscoverRepository {

    var forceError = false

    private val testData = mutableListOf(
        vietnamArticleTest,
        icelandArticleTest,
        australiaArticleTest
    )

    override suspend fun getArticlesPreview(): List<ArticlePreview> {
        if(forceError) {
            throw ExceptionGetDiscoverArticle()
        }

        return testData.map {
            it.toArticlePreview()
        }
    }

    override suspend fun getArticle(id: String): Article? {
        if(forceError) {
            throw ExceptionGetDiscoverArticle()
        }

        return testData.firstOrNull { it.id == id}
    }
}