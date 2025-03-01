package com.minhhnn18898.discover.presentation.article_detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.GetAuthStateUseCase
import com.minhhnn18898.account.test_helper.FakeAccountService
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParameters
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.test_helper.FakeDateTimeFormatter
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.discover.domain.GetArticleDetailUseCase
import com.minhhnn18898.discover.test_helper.FakeDiscoveryRepository
import com.minhhnn18898.discover.test_helper.vietnamArticleDisplayInfoTest
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailScreenViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getArticlesDiscoveryUseCase: GetArticleDetailUseCase
    private lateinit var fakeDiscoveryRepository: FakeDiscoveryRepository
    private lateinit var fakeAccountService: FakeAccountService
    private lateinit var checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase
    private lateinit var getAuthStateUseCase: GetAuthStateUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @Mock
    private lateinit var dateTimeFormatter: BaseDateTimeFormatter

    private lateinit var viewModel: ArticleDetailScreenViewModel

    @Before
    fun setup() {
        fakeDiscoveryRepository = FakeDiscoveryRepository()
        getArticlesDiscoveryUseCase = GetArticleDetailUseCase(fakeDiscoveryRepository)
        fakeAccountService = FakeAccountService()
        checkValidSignedInUserUseCase = CheckValidSignedInUserUseCase(fakeAccountService)
        getAuthStateUseCase = GetAuthStateUseCase(fakeAccountService)
        dateTimeFormatter = FakeDateTimeFormatter()
    }

    private fun setupViewModel(articleIdArg: String = "") {
        savedStateHandle = SavedStateHandle(
            mapOf("parameters" to DiscoveryArticleDetailScreenParameters(
                articleId = articleIdArg,
                articlePosition = 0,
                listArticles = emptyList()
            ))
        )

        viewModel = ArticleDetailScreenViewModel(
            savedStateHandle = savedStateHandle,
            getArticlesDiscoveryUseCase = getArticlesDiscoveryUseCase,
            baseDateTimeFormatter = dateTimeFormatter,
            checkValidSignedInUserUseCase = checkValidSignedInUserUseCase,
            getAuthStateUseCase = getAuthStateUseCase
        )
    }

    @Test
    fun getVerifiedUserState_signedInUser() {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.verifiedUserState).isTrue()
    }

    @Test
    fun getVerifiedUserState_notSignedInUser() {
        // When
        setupViewModel()

        // Then
        Truth.assertThat(viewModel.verifiedUserState).isFalse()
    }

    @Test
    fun getArticleContentState_initStateLoading() = runTest {
        // Given: set Main dispatcher to not run coroutines eagerly
        Dispatchers.setMain(StandardTestDispatcher())

        // When: init viewmodel
        setupViewModel()

        // Then: progress indicator is shown
        Truth.assertThat(viewModel.articlesContentState).isEqualTo(UiState.Loading)
    }

    @Test
    fun getArticlesContentState_loadArticleSuccess() = runTest {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        setupViewModel("1")

        // Then
        Truth.assertThat(viewModel.articlesContentState).isInstanceOf(UiState.Success::class.java)
        Truth.assertThat((viewModel.articlesContentState as UiState.Success).data).isEqualTo(vietnamArticleDisplayInfoTest)
    }

    @Test
    fun getArticlesContentState_loadNonExistedArticle() = runTest {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        setupViewModel("999")

        // Then
        Truth.assertThat(viewModel.articlesContentState).isInstanceOf(UiState.Error::class.java)
    }

    @Test
    fun getArticlesContentState_loadArticle_hasException() = runTest {
        // Given
        fakeAccountService.setCurrentUser("minhhnn@gmail.com", "123456")
        fakeDiscoveryRepository.forceError = true

        // When
        setupViewModel("1")

        // Then
        Truth.assertThat(viewModel.articlesContentState).isInstanceOf(UiState.Error::class.java)
    }
}