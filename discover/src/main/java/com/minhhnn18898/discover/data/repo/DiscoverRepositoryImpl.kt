package com.minhhnn18898.discover.data.repo

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.minhhnn18898.architecture.api.ApiResult
import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.data.model.ArticleModel
import com.minhhnn18898.firebase.gsUriToHttpsUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DiscoverRepositoryImpl @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
) : DiscoverRepository {

    companion object {
        private const val TAG = "DiscoverRepositoryImpl"
        private const val ARTICLES = "articles"
    }

    override suspend fun getArticles(): List<Article> = withContext(ioDispatcher) {
        return@withContext when(val apiResult = fetchRemoteArticles()) {
            is ApiResult.Success -> apiResult.data.map { it.toArticle() }
            is ApiResult.Error -> throw ExceptionGetDiscoverArticles()
            else -> emptyList<Article>()
        }
    }

    private suspend fun fetchRemoteArticles(): ApiResult<List<ArticleModel>> =
        suspendCoroutine { cont ->
            val db = Firebase.firestore
            val collectionRef = db.collection(ARTICLES)

            collectionRef
                .limit(50)
                .get()
                .addOnSuccessListener { result ->
                    Log.w(TAG, "Get data success ")
                    val articles = result.toObjects(ArticleModel::class.java)
                    cont.resume(ApiResult.Success(articles))
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Get failed with ", exception)
                    cont.resume(ApiResult.Error(exception))
                }
        }

    private suspend fun ArticleModel.toArticle(): Article {
        return Article(
            this.title,
            this.content,
            this.thumbUrl.gsUriToHttpsUrl(),
            this.photoUrls.gsUriToHttpsUrl(),
            this.lastEdited,
            this.originalSrc,
            this.tag
        )
    }
}