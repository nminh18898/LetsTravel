package com.minhhnn18898.discover.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.discover.data.model.ArticlePreview
import com.minhhnn18898.discover.data.model.toArticlePreview
import com.minhhnn18898.discover.data.repo.ExceptionGetDiscoverArticle
import com.minhhnn18898.discover.test_helper.FakeDiscoveryRepository
import com.minhhnn18898.discover.test_helper.australiaArticleTest
import com.minhhnn18898.discover.test_helper.icelandArticleTest
import com.minhhnn18898.discover.test_helper.vietnamArticleTest
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetListArticlesDiscoveryUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getListArticlesDiscoveryUseCase: GetListArticlesDiscoveryUseCase

    private lateinit var fakeDiscoveryRepository: FakeDiscoveryRepository

    @Before
    fun setup() {
        fakeDiscoveryRepository = FakeDiscoveryRepository()
        getListArticlesDiscoveryUseCase = GetListArticlesDiscoveryUseCase(fakeDiscoveryRepository)
    }

    @Test
    fun getValidArticles() = runTest {
        // When
        val result = getListArticlesDiscoveryUseCase.execute().toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat((result[1] as Result.Success<List<ArticlePreview>>).data).isEqualTo(mutableListOf(
            vietnamArticleTest,
            icelandArticleTest,
            australiaArticleTest
        ).map { it.toArticlePreview() })
    }

    @Test
    fun getArticles_hasException() = runTest {
        // Given
        fakeDiscoveryRepository.forceError = true

        // When
        val result = getListArticlesDiscoveryUseCase.execute().toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        Truth.assertThat((result[1] as Result.Error).exception).isInstanceOf(ExceptionGetDiscoverArticle::class.java)
    }
}