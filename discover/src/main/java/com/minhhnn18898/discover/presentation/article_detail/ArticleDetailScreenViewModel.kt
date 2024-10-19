package com.minhhnn18898.discover.presentation.article_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.GetAuthStateUseCase
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenDestination
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParameters
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParametersType
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.domain.GetArticleDetailUseCase
import com.minhhnn18898.discover.presentation.ui_models.ArticleDisplayInfo
import com.minhhnn18898.discover.presentation.ui_models.toDisplayInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

data class ArticleDetailBottomNavigationUiState(
    val previous: String,
    val next: String
)

@HiltViewModel
class ArticleDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArticlesDiscoveryUseCase: GetArticleDetailUseCase,
    private val baseDateTimeFormatter: BaseDateTimeFormatter,
    checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
): ViewModel() {

    private val parameters = savedStateHandle.toRoute<DiscoveryArticleDetailScreenDestination>(
        mapOf(typeOf<DiscoveryArticleDetailScreenParameters>() to DiscoveryArticleDetailScreenParametersType)
    ).parameters

    private var articleId: String = parameters.articleId

    private var currentPosition = parameters.articlePosition

    var verifiedUserState by mutableStateOf(checkValidSignedInUserUseCase.execute())
        private set

    var articlesContentState: UiState<ArticleDisplayInfo> by mutableStateOf(UiState.Loading)
        private set

    var articleBottomNavigationUiState by mutableStateOf(createBottomNavigationState(currentPosition))
        private set

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getAuthStateUseCase.execute().collect {
                verifiedUserState = it
                if(verifiedUserState) {
                    onUserAuthCheckSuccess()
                }
            }
        }
    }

    private fun onUserAuthCheckSuccess() {
        if(articleId.isNotBlankOrEmpty()) {
            loadDiscoveryArticle(articleId)
        }
    }

    private fun loadDiscoveryArticle(id: String) {
        viewModelScope.launch {
            getArticlesDiscoveryUseCase.execute(GetArticleDetailUseCase.Param(id)).collect {
                articlesContentState = when(it) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> handleArticleResult(it.data)
                    is Result.Error -> UiState.Error()
                }
            }
        }
    }

    private fun handleArticleResult(article: Article?): UiState<ArticleDisplayInfo> {
        if(article == null) {
            return UiState.Error()
        }

        return UiState.Success(article.toDisplayInfo(baseDateTimeFormatter))
    }

    private fun createBottomNavigationState(currentPosition: Int): ArticleDetailBottomNavigationUiState {
        val previous = parameters.listArticles.getOrNull(currentPosition - 1)?.articleTitle ?: ""
        val next = parameters.listArticles.getOrNull(currentPosition + 1)?.articleTitle ?: ""

        return ArticleDetailBottomNavigationUiState(
            previous = previous,
            next = next
        )
    }

    fun onClickNextItem() {
        val nextIndex = currentPosition + 1
        if(nextIndex in 0..< parameters.listArticles.size) {
            currentPosition = nextIndex
            articleId = parameters.listArticles.getOrNull(currentPosition)?.articleId ?: ""
            articleBottomNavigationUiState = createBottomNavigationState(currentPosition)
            loadDiscoveryArticle(articleId)
        }
    }

    fun onClickPreviousItem() {
        val previousIndex = currentPosition - 1
        if(previousIndex in 0..< parameters.listArticles.size) {
            currentPosition = previousIndex
            articleId = parameters.listArticles.getOrNull(currentPosition)?.articleId ?: ""
            articleBottomNavigationUiState = createBottomNavigationState(currentPosition)
            loadDiscoveryArticle(articleId)
        }
    }
}