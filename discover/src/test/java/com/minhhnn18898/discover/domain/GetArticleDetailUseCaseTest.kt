package com.minhhnn18898.discover.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.data.repo.ExceptionGetDiscoverArticle
import com.minhhnn18898.discover.test_helper.FakeDiscoveryRepository
import com.minhhnn18898.discover.test_helper.vietnamArticleTest
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetArticleDetailUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getArticleDetailUseCase: GetArticleDetailUseCase

    private lateinit var fakeDiscoveryRepository: FakeDiscoveryRepository

    @Before
    fun setup() {
        fakeDiscoveryRepository = FakeDiscoveryRepository()
        getArticleDetailUseCase = GetArticleDetailUseCase(fakeDiscoveryRepository)
    }

    @Test
    fun getValidArticle() = runTest {
        // When
        val result = getArticleDetailUseCase.execute(GetArticleDetailUseCase.Param("1")).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat((result[1] as Result.Success<Article?>).data).isEqualTo(vietnamArticleTest)
    }

    @Test
    fun getNonExistedArticle() = runTest {
        // When
        val result = getArticleDetailUseCase.execute(GetArticleDetailUseCase.Param("999")).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat((result[1] as Result.Success<Article?>).data).isNull()
    }

    @Test
    fun getArticle_hasException() = runTest {
        // Given
        fakeDiscoveryRepository.forceError = true

        // When
        val result = getArticleDetailUseCase.execute(GetArticleDetailUseCase.Param("1")).toList()

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
        Truth.assertThat((result[1] as Result.Error).exception).isInstanceOf(ExceptionGetDiscoverArticle::class.java)
    }
}