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
            is ApiResult.Error -> throw ExceptionGetDiscoverArticle()
            else -> emptyList<Article>()
        }
    }

    override suspend fun getArticle(id: String) = withContext(ioDispatcher) {
        return@withContext when(val apiResult = fetchRemoteArticle(id)) {
            is ApiResult.Success -> apiResult.data?.toArticle()
            is ApiResult.Error -> throw ExceptionGetDiscoverArticle()
            else -> null
        }
    }

    private suspend fun fetchRemoteArticle(id: String): ApiResult<ArticleModel?> =
        suspendCoroutine { cont ->
            val db = Firebase.firestore
            val docRef = db.collection(ARTICLES).document(id)
            docRef.get()
                .addOnSuccessListener { documentSnapshot  ->
                    if (documentSnapshot  != null) {
                        Log.w(TAG, "Get data success ")
                        val article = documentSnapshot.toObject(ArticleModel::class.java)
                        cont.resume(ApiResult.Success(article))
                    } else {
                        Log.d(TAG, "No such document")
                        cont.resume(ApiResult.Error(Exception()))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Get failed with ", exception)
                    cont.resume(ApiResult.Error(exception))
                }
        }

    private suspend fun fetchRemoteArticles(): ApiResult<List<ArticleModel>> =
        suspendCoroutine { cont ->
            val db = Firebase.firestore
            val collectionRef = db.collection(ARTICLES)

            collectionRef
                .limit(50)
                .get()
                .addOnSuccessListener { querySnapShot ->
                    Log.w(TAG, "Get data success ")
                    val result = mutableListOf<ArticleModel>()
                    querySnapShot.forEach { snapShot ->
                        result.add(snapShot.toObject(ArticleModel::class.java).copy(id = snapShot.id))
                    }
                    cont.resume(ApiResult.Success(result))
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Get failed with ", exception)
                    cont.resume(ApiResult.Error(exception))
                }
        }

    private suspend fun ArticleModel.toArticle(): Article {
        return Article(
            id = this.id,
            title = this.title,
            content = this.content,
            thumbUrl = this.thumbUrl.gsUriToHttpsUrl(),
            photoUrls = this.photoUrls.gsUriToHttpsUrl(),
            lastEdited = this.lastEdited,
            originalSrc = this.originalSrc,
            tag = this.tag
        )
    }
}